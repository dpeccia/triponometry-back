package utn.triponometry.domain.external

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.external.dtos.DistanceMatrixResponseDto
import utn.triponometry.domain.external.dtos.DistanceRow
import utn.triponometry.domain.external.dtos.Elements
import utn.triponometry.domain.external.dtos.Value
import utn.triponometry.properties.Distance
import utn.triponometry.properties.Google
import utn.triponometry.properties.TriponometryProperties
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DistanceMatrixAdapterTest {

    val distanceMatrixAdapter = DistanceMatrixAdapter()
    
    @Test
    fun `mapping the coordinates of a place`() {
        val coord = Coordinates(-34.5993652,-58.5122799)
        val response = distanceMatrixAdapter.parseReq(coord)
        val expectedResponse = "-34.5993652,-58.5122799"
        Assertions.assertEquals(expectedResponse, response)
    }

    @Test
    fun `mapping list of places`() {

        val coord = Coordinates(-34.605344, -58.492069)
        val coord2 = Coordinates(-34.592060, -58.489591)
        val coord3 = Coordinates(-34.577894, -58.484948)
        val arrayList: List<Coordinates> = listOf(coord,coord2,coord3)
        val response = distanceMatrixAdapter.mapArrayToString(arrayList)
        val expectedResponse = "-34.605344,-58.492069|-34.59206,-58.489591|-34.577894,-58.484948"
        Assertions.assertEquals(expectedResponse, response)

    }

    @Test
    fun `mapping distance matrix response`() {

        val file = File("src/test/resources/distance_matrix.json")
        val fileContent = file.readText()
        val matrix = Gson().fromJson(fileContent, DistanceMatrixResponseDto::class.java)

        val places = distanceMatrixAdapter.matrixToListOfPlaces(matrix)

        Assertions.assertEquals("Baigorria 3263, C1417 FRK, Buenos Aires, Argentina",places[0].name)
        Assertions.assertEquals(0, places[0].id)
        Assertions.assertEquals(4168, places[0].distances[1])
        Assertions.assertEquals(1578, places[0].distances[2])
        Assertions.assertEquals(5027, places[0].distances[3])

        Assertions.assertEquals("Acceso A Patricias Argentinas 171, C1414 CABA, Argentina",places[1].name)
        Assertions.assertEquals(1, places[1].id)
        Assertions.assertEquals(4200, places[1].distances[0])
        Assertions.assertEquals(5537, places[1].distances[2])
        Assertions.assertEquals(1082, places[1].distances[3])

    }
}

