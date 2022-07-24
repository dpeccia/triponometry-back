package utn.triponometry.domain.external

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import utn.triponometry.properties.TriponometryProperties
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeocodeTest {

    @Test
    fun `getting the coordinates of a place`() {
        val file = File("src/test/resources/google_coordinates_successful_response.json")
        val fileContent = file.readText()
        val expectedLatitude = -34.5993652
        val expectedLongitude = -58.5122799

        val googleMock = mock(GoogleApi::class.java)
        val geocode = Geocode(TriponometryProperties(),googleMock)

        `when`(googleMock.getCoordinatesFromGeocodeApi("Plaza Arenales")).thenReturn(fileContent)

        val coordinates = geocode.getCoordinates("Plaza Arenales")
        Assertions.assertEquals(expectedLatitude, coordinates.latitude)
        Assertions.assertEquals(expectedLongitude, coordinates.longitude)

    }

    @Test
    fun `getting the coordinates of a place throws an error`() {
        val file = File("src/test/resources/google_coordinates_failure_response.json")
        val fileContent = file.readText()


        val googleMock = mock(GoogleApi::class.java)
        val geocode = Geocode(TriponometryProperties(),googleMock)

        `when`(googleMock.getCoordinatesFromGeocodeApi("Plaza Arenales")).thenReturn(fileContent)

        try {
           geocode.getCoordinates("Plaza Arenales")
        } catch (e: Exception) {
            Assertions.assertEquals("No results were found", e.message)
        }
    }


}

