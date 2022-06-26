package utn.triponometry.domain.external

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.external.dtos.CurrentWeatherDto
import utn.triponometry.domain.external.dtos.GeocodingDto
import utn.triponometry.helpers.OpenWeatherException
import utn.triponometry.properties.TriponometryProperties

@Component
class OpenWeather(triponometryProperties: TriponometryProperties, private val objectMapper: ObjectMapper) {
    private val baseUrl = triponometryProperties.weather.url
    private val apiKey = triponometryProperties.weather.apiKey

    fun getCoordinates(city: String): Coordinates {
        val geocodingResponse = getFromOpenWeatherApi("/geo/1.0/direct?q=$city&limit=1")
        val geocoding = objectMapper.readValue<List<GeocodingDto>>(geocodingResponse).first()

        return Coordinates(geocoding.lat, geocoding.lon)
    }

    fun getCurrentWeather(city: String, coordinates: Coordinates): String {
        val endpoint = "/data/2.5/weather?lat=${coordinates.latitude}&lon=${coordinates.longitude}"
        val currentWeatherResponse = getFromOpenWeatherApi(endpoint)
        val currentWeather = objectMapper.readValue<CurrentWeatherDto>(currentWeatherResponse)

        return """
            The current weather for the city "$city" is: ${currentWeather.weather.first().description}
        """.trimIndent()
    }

    private fun getFromOpenWeatherApi(endpoint: String) =
        WebClient.create(baseUrl).get()
            .uri("$endpoint&appid=$apiKey")
            .retrieve()
            .onStatus(HttpStatus::isError) {
                val statusCode = it.statusCode()
                throw OpenWeatherException("${statusCode.value()} - ${statusCode.reasonPhrase}")
            }
            .bodyToMono(String::class.java)
            .block() ?: throw OpenWeatherException("There was an error with the OpenWeather Server")
}