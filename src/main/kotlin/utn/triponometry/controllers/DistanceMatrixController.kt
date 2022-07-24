package utn.triponometry.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.Coordinates
import utn.triponometry.services.DistanceMatrixService

@RestController
@RequestMapping("/location")
class DistanceMatrixController(private val distanceMatrixService: DistanceMatrixService) {
    @PostMapping("/distance")
    fun getDistance(@RequestBody coordinatesmap: List<Coordinates>): ResponseEntity<Any> {
        val response = distanceMatrixService.getDistanceMatrixData(coordinatesmap)
        return ResponseEntity.ok(response)
    }
}