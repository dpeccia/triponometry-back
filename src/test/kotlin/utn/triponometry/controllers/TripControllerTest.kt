package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.maps.model.TravelMode
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.CalculatorOutputs
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.PlaceInput
import utn.triponometry.helpers.GoogleDistanceMatrixApiException
import utn.triponometry.services.TripService

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {
    @SpykBean
    lateinit var tripService: TripService

    @SpykBean
    lateinit var tripController: TripController

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val placesInputs = listOf(PlaceInput(Coordinates(-34.3, -35.2), 120), PlaceInput(Coordinates(-45.2, -34.4), 120))

    val calculatorInputs = CalculatorInputs(10, 2, 600, TravelMode.DRIVING, placesInputs)

    @Test
    fun `optimal-route endpoint needs authorization`() {
        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/optimal-route")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculatorInputs))
        ).andExpect(status().`is`(401)).andReturn().response.contentAsString

        val error = objectMapper.readValue<Map<String, String>>(responseAsString)

        assertEquals("Token not found or expired. Login again", error["error"])
    }

    @Test
    fun `optimal-route endpoint returns an error`() {
        stubLogin()
        every { tripService.getDurationBetween(any(), any()) } throws GoogleDistanceMatrixApiException("There was an error")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/optimal-route")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculatorInputs))
        ).andExpect(status().`is`(500)).andReturn().response.contentAsString

        val error = objectMapper.readValue<Map<String, String>>(responseAsString)

        assertEquals("There was an error", error["error"])
    }

    @Test
    fun `optimal-route endpoint returns OK`() {
        stubLogin()
        every { tripService.calculateOptimalRoute(any()) } returns CalculatorOutputs("1234")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/optimal-route")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculatorInputs))
        ).andExpect(status().`is`(200)).andReturn().response.contentAsString

        val calculatorOutputs = objectMapper.readValue<CalculatorOutputs>(responseAsString)

        assertEquals("1234", calculatorOutputs.mapId)
    }

    fun stubLogin() {
        every { tripController.checkAndGetUserId(any()) } returns ObjectId()
    }
}