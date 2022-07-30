package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place
import utn.triponometry.helpers.GoogleDistanceMatrixApiException
import utn.triponometry.services.TripService
import java.io.File

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {
    @MockkBean
    lateinit var tripService: TripService

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private final val hotel = Place(0, "Hotel", mapOf(1 to 30, 2 to 60, 3 to 35))
    private final val colloseum = Place(1, "Colloseum", mapOf(0 to 25, 2 to 20, 3 to 25), 120)
    private final val fontanaDiTrevi = Place(2, "Fontana Di Trevi", mapOf(0 to 50, 1 to 20, 3 to 25), 180)
    private final val vaticano = Place(3, "Vaticano", mapOf(0 to 40, 1 to 30, 2 to 30), 120)

    val places = listOf(hotel, colloseum, fontanaDiTrevi, vaticano)
    val coordinates = listOf(Coordinates(-34.3, -35.2), Coordinates(-45.2, -34.4))

    @Test
    fun `duration endpoint returns OK`() {
        every { tripService.getDurationBetween(any(), any()) } returns places

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/durations/DRIVING")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coordinates))
        ).andExpect(status().`is`(200)).andReturn().response.contentAsString

        val response: List<Place> = objectMapper.readValue(responseAsString)

        assertEquals(places, response)
    }

    @Test
    fun `duration endpoint returns an error`() {
        every { tripService.getDurationBetween(any(), any()) } throws GoogleDistanceMatrixApiException("There was an error")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/durations/DRIVING")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coordinates))
        ).andExpect(status().`is`(500)).andReturn().response.contentAsString

        assertEquals("There was an error", responseAsString)
    }

    @Test
    fun `optimal-route endpoint returns OK`() {
        val calculatorInputs = CalculatorInputs(10, 2, 600, listOf(hotel, colloseum, fontanaDiTrevi, vaticano))

        every { tripService.calculateOptimalRoute(any()) } returns listOf(
            Day(1, mutableListOf(hotel, colloseum, vaticano)),
            Day(2, mutableListOf(hotel, fontanaDiTrevi))
        )

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/optimal-route")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculatorInputs))
        ).andExpect(status().`is`(200)).andReturn().response.contentAsString

        val expectedResponse = File("src/test/resources/optimal_route_response.json").readText()

        JSONAssert.assertEquals(expectedResponse, responseAsString, false)
    }
}