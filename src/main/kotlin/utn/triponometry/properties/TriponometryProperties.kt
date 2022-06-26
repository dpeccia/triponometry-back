package utn.triponometry.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "triponometry")
data class TriponometryProperties (
    val weather: Weather = Weather()
)

data class Weather(var url: String = "", var apiKey: String = "")