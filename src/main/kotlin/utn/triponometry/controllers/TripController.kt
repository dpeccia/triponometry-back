package utn.triponometry.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.services.TripService

@RestController
@RequestMapping("/trip")
class TripController(private val tripService: TripService) {
    @PostMapping("/test/genetic-algorithm")
    fun testGeneticAlgorithm(): ResponseEntity<Any> {
        val bestRoute = tripService.runGeneticAlgorithm()
        return ResponseEntity.ok("Best: (Fitness = ${bestRoute.fitness}, Time = ${bestRoute.totalTime}) ${bestRoute.places.map { it.id }}")
    }
}