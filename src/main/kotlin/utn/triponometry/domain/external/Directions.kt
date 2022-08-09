package utn.triponometry.domain.external

import BalloonStyle
import DocumentDto
import FolderDto
import LineString
import LineStyle
import PairDto
import PlacemarkDto
import PlacemarkInterface
import PlacemarkRouteDto
import PointDto
import Style
import StyleMap
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.google.maps.model.TravelMode
import kml
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
import org.springframework.stereotype.Component
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Day
import utn.triponometry.properties.TriponometryProperties
import java.io.File
import java.util.*


@Component
class Directions(triponometryProperties: TriponometryProperties, private val googleApi: GoogleApi) {
    private val apiKey = triponometryProperties.google.apiKey
    val colors = listOf("ffff6712", "ff42b37c", "ffb0279c", "ff5252ff","ffd18802","ff485579","ff5b18c2","ffb18ff4")

    fun getDirections(coordinatesO: Coordinates, visit: MutableList<Coordinates?>, travelMode: TravelMode): String {
        val results = googleApi.getDirectionsApi(coordinatesO, visit,travelMode)
        var road = ""
        results?.routes?.forEach { r ->
            r.legs.forEach { l ->
                l.steps.forEach { s -> road += "\t\t\t${s.startLocation.lng},${s.startLocation.lat},0\n\t\t\t${s.endLocation.lng},${s.endLocation.lat},0\n" }
            }
        }
        return road
    }

    fun makeKMLFile(days: List<Day>, travelMode: TravelMode): kml {

       val kml = createBaseXml()
        val placemarks = mutableListOf<PlacemarkDto>()

            days.forEach {
                day -> day.route.forEach {
                p -> placemarks.add(PlacemarkDto(p.name,"#icon-1899-0288D1-nodesc",
                PointDto(coordinatesToString(p.coordinates!!))))
        }}
        val folders = mutableListOf<FolderDto>()
        val folder = FolderDto("Lugares de interés",placemarks.toList())

       folders.add(folder)
        kml.document.folder = folders

        days.forEach { d -> getDirectionsOfDay(kml,d,travelMode)}
        return kml
    }


    fun createKMLFile(days: List<Day>, travelMode: TravelMode): String {

        val kml = makeKMLFile(days, travelMode)
//        val xmlModule = JacksonXmlModule()
//        xmlModule.setDefaultUseWrapper(false)

        val builder = Jackson2ObjectMapperBuilder()
        val mapper = builder.createXmlMapper(true).build<ObjectMapper>()
        (mapper as XmlMapper).enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        mapper.registerModule(ParameterNamesModule())
        mapper.setDefaultUseWrapper(false)

        return mapper.writeValueAsString(kml)

    }

    fun getDirectionsOfDay(kml: kml, days: Day, travelMode: TravelMode) {
        val listWithoutOrigin = days.route.map { l -> l.coordinates }.toMutableList()
        val indications = getDirections(listWithoutOrigin.removeAt(0)!!,listWithoutOrigin!!,travelMode)

        val placemarks = mutableListOf<PlacemarkInterface>()

        val color = colors[0]
        Collections.rotate(colors, -1);

        //adding style for this specific line
        val style1 = Style("line-${color}-5000-nodesc-normal",BalloonStyle(),LineStyle(color,5))
        val style2 = Style("line-${color}-5000-nodesc-highlight",BalloonStyle(),LineStyle(color,7))
        val styleMap2 = StyleMap("line-${color}-5000-nodesc",mutableListOf(
            PairDto("normal","#line-${color}-5000-nodesc-normal"),
            PairDto("highlight","#line-${color}-5000-nodesc-highlight")
        ))

        placemarks.add(PlacemarkRouteDto("Recorrido","#line-${color}-5000-nodesc",LineString(indications,1)))

        days.route.forEach { p -> placemarks.add(PlacemarkDto(p.name,"#icon-1899-0288D1-nodesc",
            PointDto(coordinatesToString(p.coordinates!!))))}

        kml.document.folder?.add(FolderDto("Dia ${days.number}",placemarks.toList()))
        kml.document.styles?.addAll(listOf(style1,style2))
        kml.document.styleMaps?.add(styleMap2)
    }


    fun createBaseXml(): kml {
        val styleMap1 = StyleMap("icon-1899-0288D1-nodesc",mutableListOf<PairDto>(
            PairDto("normal","#icon-1899-0288D1-nodesc-normal"),
            PairDto("highlight","#icon-1899-0288D1-nodesc-highlight")
        ))
        val doc = DocumentDto(
            "Triponometry",
            "Recorrido Optimo",
            null,
            mutableListOf(),
            mutableListOf(styleMap1),
        )
        return kml(doc,"http://www.opengis.net/kml/2.2")
    }

    fun coordinatesToString(location: Coordinates): String{
        return "${location.longitude},${location.latitude},0"
    }
}

