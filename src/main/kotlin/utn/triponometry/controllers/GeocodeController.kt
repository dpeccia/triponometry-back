package utn.triponometry.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.services.GeocodeService

@RestController
@RequestMapping("/location")
class GeocodeController(private val geocodeService: GeocodeService) {
    @GetMapping("/{place}")
    fun getCurrentWeatherData(@PathVariable place: String): ResponseEntity<Any> {
        val response = geocodeService.getCoordenatesData(place)
        return ResponseEntity.ok(response)
    }
}