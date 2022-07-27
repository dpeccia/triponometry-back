package utn.triponometry.controllers

import com.google.maps.model.TravelMode
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Place
import utn.triponometry.services.TripService

@RestController
@RequestMapping("/trip")
class TripController(private val tripService: TripService) {
    @PostMapping("/durations/{travelMode}")
    @ApiOperation("Gets the duration between each coordinate")
    fun getDurationsBetweenCoordinates(@PathVariable travelMode: TravelMode, @RequestBody coordinates: List<Coordinates>): ResponseEntity<Any> {
        val places = tripService.getDurationBetween(coordinates, travelMode)
        return ResponseEntity.ok(places)
    }

    @PostMapping("/optimal-route")
    @ApiOperation("Calculates the optimal route between a list of places")
    fun calculateOptimalRoute(@RequestBody places: List<Place>): ResponseEntity<Any> {
        val bestRoute = tripService.calculateOptimalRoute(places)
        return ResponseEntity.ok("Best Route: ${bestRoute.places.map { it.id }}")
    }
}