package utn.triponometry.services

import helpers.FakeOpenWeatherExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import utn.triponometry.config.AppConfig
import utn.triponometry.domain.external.OpenWeather
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.properties.Weather

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherServiceTest {
    private val port = 8090

    @JvmField
    @RegisterExtension
    val fakeOpenWeather = FakeOpenWeatherExtension(port)

    private val triponometryProperties = TriponometryProperties(
        weather = Weather("http://localhost:$port/", "1234")
    )
    private val openWeather = OpenWeather(triponometryProperties, AppConfig().objectMapper())
    private val weatherService = WeatherService(openWeather)

    @Test
    fun `getting the current weather of a city`() {
        fakeOpenWeather.`stub scenario when get coordinates works successfully`()
        fakeOpenWeather.`stub scenario when get weather works successfully`()

        val weather = weatherService.getCurrentWeatherData("New York")

        assertEquals("The current weather for the city \"New York\" is: clear sky", weather)
    }
}