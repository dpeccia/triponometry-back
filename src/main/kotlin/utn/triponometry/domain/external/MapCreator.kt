package utn.triponometry.domain.external

import BalloonStyle
import DocumentDto
import FolderDto
import Icon
import IconStyle
import LineString
import LineStyle
import PlacemarkDto
import PlacemarkInterface
import PlacemarkRouteDto
import PointDto
import Style
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.google.maps.model.TravelMode
import hotSpot
import kml
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Day

@Component
class MapCreator(private val googleApi: GoogleApi) {
    private final val opacity = "ff" // 00 (transparent) to FF (opaque)

    val kmlColors = listOf(
        "${opacity}8080ff", "${opacity}58b3fc", "${opacity}00eaff", "${opacity}74fc8b",
        "${opacity}FFF69B", "${opacity}FFC4A0", "${opacity}FFB2BD", "${opacity}ffa8ff", "${opacity}FCFFFF"
    )

    fun createKMLFile(days: List<Day>, travelMode: TravelMode): String {
        val kml = createBaseXml(days, travelMode)

        val builder = Jackson2ObjectMapperBuilder()
        val mapper = builder.createXmlMapper(true).build<ObjectMapper>()
        (mapper as XmlMapper).enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
        mapper.registerModule(ParameterNamesModule())
        mapper.setDefaultUseWrapper(false)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        return mapper.writeValueAsString(kml)
    }

    private fun createBaseXml(days: List<Day>, travelMode: TravelMode): kml {
        val styles = mutableListOf<Style>()

        // Icons taken from: http://kml4earth.appspot.com/icons.html#mapfiles

        val hotelIconStyle = Style(
            id = "icon-1602-000000-nodesc",
            iconStyle = IconStyle(
                color = "fffafafa",
                scale = 1,
                icon = Icon("http://maps.google.com/mapfiles/kml/paddle/wht-stars.png")
            ),
            balloonStyle = BalloonStyle()
        )
        styles.add(hotelIconStyle)

        days.forEach { day ->
            val dayColor = kmlColors[day.number - 1]
            val hexColor = toHexColor(dayColor)

            val iconStyle = Style(
                id = "icon-1899-$hexColor-nodesc",
                iconStyle = IconStyle(
                    color = dayColor,
                    scale = 1,
                    icon = Icon("http://maps.google.com/mapfiles/kml/paddle/wht-circle.png"),
                    hotSpot = hotSpot()
                ),
                balloonStyle = BalloonStyle()
            )

            val lineStyle = Style(
                id = "line-$dayColor-5000-nodesc",
                lineStyle = LineStyle(dayColor, 5.0),
                balloonStyle = BalloonStyle()
            )

            styles.add(iconStyle)
            styles.add(lineStyle)
        }

        val folder = days.map { getKmlFolder(it, travelMode) }.toMutableList()

        val doc = DocumentDto("Triponometry", "Recorrido Optimo", folder, styles, null)
        return kml(doc,"http://www.opengis.net/kml/2.2")
    }

    private fun getKmlFolder(day: Day, travelMode: TravelMode): FolderDto {
        val listWithoutOrigin = day.route.map { it.coordinates }.toMutableList()
        val indications = getDirectionsFromGoogle(listWithoutOrigin.removeAt(0)!!,listWithoutOrigin!!,travelMode)

        val placemarks = mutableListOf<PlacemarkInterface>()

        val color = kmlColors[day.number - 1]
        val hexColor = toHexColor(color)

        placemarks.add(PlacemarkRouteDto("Recorrido Dia ${day.number}","#line-${color}-5000-nodesc",LineString(indications,1)))

        day.route.forEach { place ->
            val icon = if (place.id == 0) "1602-000000-nodesc" else "1899-$hexColor-nodesc"
            placemarks.add(PlacemarkDto(place.name,"#icon-${icon}", PointDto(coordinatesToString(place.coordinates!!))))
        }

        return FolderDto("Dia ${day.number}", placemarks.toList())
    }

    private fun getDirectionsFromGoogle(coordinatesO: Coordinates, visit: MutableList<Coordinates?>, travelMode: TravelMode): String {
        val results = googleApi.getDirectionsApi(coordinatesO, visit,travelMode)
        var road = ""
        results?.routes?.forEach { r ->
            r.legs.forEach { l ->
                l.steps.forEach {
                        s ->
                    val coordinates = s.polyline.decodePath()
                   coordinates.forEach{ c -> road += "\t\t\t${c.lng},${c.lat}\n" }
                }
            }
        }
        return road
    }

    // KML color to Hexa color -> https://stackoverflow.com/a/59661064
    private fun toHexColor(kmlColor: String) = kmlColor.chunked(2).reversed().joinToString("").take(6)

    private fun coordinatesToString(location: Coordinates): String{
        return "${location.longitude},${location.latitude},0"
    }
}

