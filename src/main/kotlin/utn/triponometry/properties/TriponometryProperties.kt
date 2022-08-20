package utn.triponometry.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import utn.triponometry.domain.external.Geocode

@ConfigurationProperties(prefix = "triponometry")
data class TriponometryProperties(
    val weather: Weather = Weather(),
    val google: Google = Google(),
    val distance: Distance = Distance(),
    val geneticAlgorithm: GeneticAlgorithm = GeneticAlgorithm(),
    val aws: Aws = Aws(),
    val hash: Hash = Hash()
)

data class Weather(var url: String = "", var apiKey: String = "")
data class Distance(var url: String = "")
data class Google(var apiKey: String = "")
data class GeneticAlgorithm(var individualsQty: Int = 1000, val cyclesQty: Int = 100)
data class Aws(var url: String = "")
data class Hash(var salt: String = "")
