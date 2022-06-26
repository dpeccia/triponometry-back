package utn.triponometry.domain.external

import helpers.FakeOpenWeatherExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import utn.triponometry.config.AppConfig
import utn.triponometry.domain.Coordinates
import utn.triponometry.helpers.OpenWeatherException
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.properties.Weather

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenWeatherTest {
    private val port = 8090

    @JvmField
    @RegisterExtension
    val fakeOpenWeather = FakeOpenWeatherExtension(port)

    private val triponometryProperties = TriponometryProperties(
        weather = Weather("http://localhost:$port/", "1234")
    )
    private val openWeather = OpenWeather(triponometryProperties, AppConfig().objectMapper())

    @Test
    fun `getting the coordinates of a city`() {
        fakeOpenWeather.`stub scenario when get coordinates works successfully`()

        val coordinates = openWeather.getCoordinates("New York")

        val expectedCoordinates = Coordinates(40.4167047, -3.7035825)
        assertEquals(expectedCoordinates, coordinates)
    }

    @Test
    fun `getting the coordinates of a city throws an error`() {
        fakeOpenWeather.`stub scenario when get coordinates fails`()

        val exception = assertThrows<OpenWeatherException> { openWeather.getCoordinates("New York") }
        assertEquals("401 - Unauthorized", exception.message)
    }

    @Test
    fun `getting the current weather of a city`() {
        fakeOpenWeather.`stub scenario when get weather works successfully`()

        val weather = openWeather.getCurrentWeather("New York", Coordinates(40.3, -3.2))

        assertEquals("The current weather for the city \"New York\" is: clear sky", weather)
    }

    @Test
    fun `getting the current weather of a city throws an error`() {
        fakeOpenWeather.`stub scenario when get weather fails`()

        val exception = assertThrows<OpenWeatherException> { openWeather.getCurrentWeather("New York", Coordinates(40.3, -3.2)) }
        assertEquals("400 - Bad Request", exception.message)
    }
}