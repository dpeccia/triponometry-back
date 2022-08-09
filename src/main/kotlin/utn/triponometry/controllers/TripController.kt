package utn.triponometry.controllers

import com.google.maps.model.TravelMode
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Day
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
    @ApiOperation("Calculates the optimal route for the given calculator inputs")
    fun calculateOptimalRoute(@RequestBody calculatorInputs: CalculatorInputs): ResponseEntity<Any> {
        val optimalRoute = tripService.calculateOptimalRoute(calculatorInputs)

        return ResponseEntity.ok(optimalRoute)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/kmlFile/{travelMode}",produces = [MediaType.APPLICATION_XML_VALUE])
    @ApiOperation("Gets the kml file with the recommended route")
    fun getMap(@PathVariable travelMode: TravelMode, @RequestBody days: List<Day>): ResponseEntity<String> {
        val response = tripService.getMapFileData(days,travelMode)
        return ResponseEntity.ok(response)
    }
}