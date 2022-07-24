package utn.triponometry.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import utn.triponometry.domain.external.Geocode

@ConfigurationProperties(prefix = "triponometry")
data class TriponometryProperties(
        val weather: Weather = Weather(),
        val google: Google = Google(),
        val distance: Distance = Distance()

)

data class Weather(var url: String = "", var apiKey: String = "")
data class Distance(var url: String = "")
data class Google(var apiKey: String = "")
