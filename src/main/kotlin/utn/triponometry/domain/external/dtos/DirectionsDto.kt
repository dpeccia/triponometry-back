import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


data class Icon (
     var href: String = "https://www.gstatic.com/mapspro/images/stock/503-wht-blank_maps.png"
)

data class hotSpot (
    @JacksonXmlProperty(isAttribute = true)
     var x : String = "32",
    @JacksonXmlProperty(isAttribute = true)
     var xunits: String = "pixels",
    @JacksonXmlProperty(isAttribute = true)
     var y: String = "64",
    @JacksonXmlProperty(isAttribute = true)
     var yunits: String = "insetPixels"
)

data class IconStyle (
    var color: String,
    var scale: Int = 0,
    @JacksonXmlProperty(localName = "Icon")
    var icon: Icon,
    var hotSpot: hotSpot? = null
)

data class LabelStyle (
    var scale: Int = 0
)

data class BalloonStyle (
    @JacksonXmlCData
    val text: String = "<h3>$[name]</h3>"
)

data class Style (
    @JacksonXmlProperty(isAttribute = true)
    var id: String? = null,
    @JacksonXmlProperty(localName = "IconStyle")
    var iconStyle: IconStyle? = null,
    @JacksonXmlProperty(localName = "LabelStyle")
    var labelStyle: LabelStyle? = null,
    @JacksonXmlProperty(localName = "BalloonStyle")
    var balloonStyle: BalloonStyle? = null,
    @JacksonXmlProperty(localName = "LineStyle")
    var lineStyle: LineStyle? = null
)

data class PairDto (
    var key: String,
    var styleUrl: String
)

data class StyleMap (
    @JacksonXmlProperty(isAttribute = true)
    var id: String,
    @JacksonXmlProperty(localName = "Pair")
    var pair: MutableList<PairDto>,
)

data class LineStyle(
    var color: String,
    var width: Double
)

data class PointDto (
      var coordinates: String
)

data class PlacemarkDto(
    var name: String,
    var styleUrl: String  ?  = null,
    @JacksonXmlProperty(localName = "Point")
    var point: PointDto?,
): PlacemarkInterface

data class PlacemarkRouteDto(
    var name: String,
    var styleUrl: String  ?  = null,
    @JacksonXmlProperty(localName = "LineString")
    var lineString: LineString  ?  = null
): PlacemarkInterface

interface PlacemarkInterface{
}

data class FolderDto (
     var name: String,
     @JacksonXmlProperty(localName = "Placemark")
     var placemark: List<PlacemarkInterface>
)

data class LineString (
    var coordinates: String,
    var tessellate: Int = 1
)

data class DocumentDto (
    var name: String,
    var description: String,
    @JacksonXmlProperty(localName = "Folder")
    var folder: MutableList<FolderDto>? = null,
    @JacksonXmlProperty(localName = "Style")
    var styles: MutableList<Style>? = null,
    @JacksonXmlProperty(localName = "StyleMap")
    var styleMaps: MutableList<StyleMap>? = null
)

data class kml(
    @JacksonXmlProperty(localName = "Document")
    var document: DocumentDto,
    @JacksonXmlProperty(isAttribute = true)
    var xmlns: String
)
