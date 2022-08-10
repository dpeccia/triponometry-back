package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.maps.model.TravelMode
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
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
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.PlaceInput
import utn.triponometry.helpers.GoogleDistanceMatrixApiException
import utn.triponometry.services.TripService

@SpringBootTest
@AutoConfigureMockMvc
class TripControllerTest {
    @SpykBean
    lateinit var tripService: TripService

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val placesInputs = listOf(PlaceInput(Coordinates(-34.3, -35.2), 120), PlaceInput(Coordinates(-45.2, -34.4), 120))

    val calculatorInputs = CalculatorInputs(10, 2, 600, TravelMode.DRIVING, placesInputs)

    @Test
    fun `duration endpoint returns an error`() {
        every { tripService.getDurationBetween(any(), any()) } throws GoogleDistanceMatrixApiException("There was an error")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/optimal-route")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculatorInputs))
        ).andExpect(status().`is`(500)).andReturn().response.contentAsString

        assertEquals("There was an error", responseAsString)
    }

    @Test
    fun `optimal-route endpoint returns OK`() {
        every { tripService.calculateOptimalRoute(any()) } returns ""

        mvc.perform(
            MockMvcRequestBuilders
                .post("/trip/optimal-route")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(calculatorInputs))
        ).andExpect(status().`is`(200))
    }
}