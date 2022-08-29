package utn.triponometry.domain.external

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.PlaceInput
import utn.triponometry.domain.external.dtos.DistanceMatrixResponseDto
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistanceMatrixAdapterTest {

    val distanceMatrixAdapter = DistanceMatrixAdapter()
    
    @Test
    fun `mapping the coordinates of a place`() {
        val coord = Coordinates(-34.5993652,-58.5122799)
        val response = distanceMatrixAdapter.parseReq(coord)
        val expectedResponse = "-34.5993652,-58.5122799"
        assertEquals(expectedResponse, response)
    }

    @Test
    fun `mapping list of places`() {

        val coord = Coordinates(-34.605344, -58.492069)
        val coord2 = Coordinates(-34.592060, -58.489591)
        val coord3 = Coordinates(-34.577894, -58.484948)
        val arrayList: List<Coordinates> = listOf(coord,coord2,coord3)
        val response = distanceMatrixAdapter.mapArrayToString(arrayList)
        val expectedResponse = "-34.605344,-58.492069|-34.59206,-58.489591|-34.577894,-58.484948"
        assertEquals(expectedResponse, response)

    }

    @Test
    fun `mapping distance matrix response`() {
        val file = File("src/test/resources/distance_matrix.json")
        val fileContent = file.readText()
        val matrix = Gson().fromJson(fileContent, DistanceMatrixResponseDto::class.java)

        val placesInputs = listOf(
            PlaceInput("Hotel", Coordinates(-34.598682,-58.511546), 120),
            PlaceInput("Activity 1", Coordinates(-34.616954,-58.433532), 60),
            PlaceInput("Activity 2", Coordinates(-34.605102,-58.493364), 40),
            PlaceInput("Activity 3", Coordinates(-34.606149,-58.438509), 30)
        )

        val places = distanceMatrixAdapter.matrixToListOfPlaces(matrix, placesInputs)

        assertEquals("Hotel",places[0].name)
        assertEquals(0, places[0].id)
        assertEquals(69, places[0].durations[1])
        assertEquals(26, places[0].durations[2])
        assertEquals(83, places[0].durations[3])
        assertEquals(120, places[0].timeSpent)

        assertEquals("Activity 1",places[1].name)
        assertEquals(1, places[1].id)
        assertEquals(70, places[1].durations[0])
        assertEquals(92, places[1].durations[2])
        assertEquals(18, places[1].durations[3])
        assertEquals(60, places[1].timeSpent)
    }
}

