package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.services.TripService

@RestController
@RequestMapping("/trip")
class TripController(private val tripService: TripService) {
    @PostMapping("/optimal-route", produces = [MediaType.APPLICATION_XML_VALUE])
    @ApiOperation("Calculates the optimal route for the given calculator inputs")
    fun calculateOptimalRoute(@RequestBody calculatorInputs: CalculatorInputs): ResponseEntity<Any> {
        val optimalRouteAsKml = tripService.calculateOptimalRoute(calculatorInputs)
        return ResponseEntity.ok(optimalRouteAsKml)
    }
}