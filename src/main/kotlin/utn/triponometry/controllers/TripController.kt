package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.services.TripService
import java.lang.RuntimeException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/trip", produces = [MediaType.APPLICATION_JSON_VALUE])
class TripController(private val tripService: TripService): BaseController() {
    @PostMapping("/optimal-route")
    @ApiOperation("Calculates the optimal route for the given calculator inputs and returns an ID")
    fun calculateOptimalRoute(@RequestBody calculatorInputs: CalculatorInputs, request: HttpServletRequest): ResponseEntity<Any> {
        checkAndGetUserId(request)
        val optimalRouteId = tripService.calculateOptimalRoute(calculatorInputs)
        return ResponseEntity.ok(optimalRouteId)
    }

    @GetMapping("/kml/{kmlId}")
    fun getKmlInformation(@PathVariable kmlId: String): ResponseEntity<Any> {
        val response = tripService.getAgendaFromAws(kmlId)
        return ResponseEntity.ok(response)
    }
}