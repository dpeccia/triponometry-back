package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import utn.triponometry.helpers.OpenWeatherException
import utn.triponometry.services.WeatherService

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {
    @MockkBean
    lateinit var weatherService: WeatherService

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `current weather endpoint returns OK`() {
        val city = "New York"
        val expectedResponse = "The current weather for the city \"$city\" is: clear sky"
        every { weatherService.getCurrentWeatherData(city) } returns expectedResponse

        val response = mvc.perform(
            MockMvcRequestBuilders
                .get("/weather/current/$city")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(200)).andReturn().response

        assertEquals(expectedResponse, response.contentAsString)
    }

    @Test
    fun `current weather endpoint returns an error`() {
        val city = "New York"
        val expectedResponse = "There was an error"
        every { weatherService.getCurrentWeatherData(city) } throws OpenWeatherException(expectedResponse)

        val response = mvc.perform(
            MockMvcRequestBuilders
                .get("/weather/current/$city")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(500)).andReturn().response.contentAsString

        val error = objectMapper.readValue<Map<String, String>>(response)

        assertEquals(expectedResponse, error["error"])
    }
}