package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.services.TripService
import java.lang.RuntimeException

@RestController
@RequestMapping("/trip")
class TripController(private val tripService: TripService) {
    @PostMapping("/optimal-route")
    @ApiOperation("Calculates the optimal route for the given calculator inputs and returns an ID")
    fun calculateOptimalRoute(@RequestBody calculatorInputs: CalculatorInputs): ResponseEntity<Any> {
        val optimalRouteId = tripService.calculateOptimalRoute(calculatorInputs)
        return ResponseEntity.ok(optimalRouteId)
    }
}