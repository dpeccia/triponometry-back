package utn.triponometry.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.services.WeatherService

@RestController
@RequestMapping("/weather")
class WeatherController(private val weatherService: WeatherService) {
    @GetMapping("/current/{city}")
    fun getCurrentWeatherData(@PathVariable city: String): ResponseEntity<Any> {
        val response = weatherService.getCurrentWeatherData(city)
        return ResponseEntity.ok(response)
    }
}