package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.TripStatus
import utn.triponometry.domain.dtos.NewTripRequest
import utn.triponometry.services.TripService
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

    @PostMapping
    @ApiOperation("Creates a new trip")
    fun createNewTrip(@RequestBody newTripRequest: NewTripRequest, request: HttpServletRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val response = tripService.createNewTrip(newTripRequest, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/all")
    @ApiOperation("Returns all trips")
    fun getAllTripsFromUser(request: HttpServletRequest): ResponseEntity<Any> {
        checkAndGetUserId(request)
        val response = tripService.getAllTrips()
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @ApiOperation("Returns user's separated by status")
    fun getTripsFromUser(request: HttpServletRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val response = tripService.getTrips(userId)
        return ResponseEntity.ok(response)

    }

    @PutMapping
    @ApiOperation("Update a trip status")
    fun updateTripStatus(request: HttpServletRequest,id: String, newStatus: TripStatus): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val tripId = ObjectId(id)
        val response = tripService.updateTripStatus(userId,tripId,newStatus)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/kml/{kmlId}")
    fun getKmlInformation(@PathVariable kmlId: String): ResponseEntity<Any> {
        val response = tripService.getAgendaFromAws(kmlId)
        return ResponseEntity.ok(response)
    }
}