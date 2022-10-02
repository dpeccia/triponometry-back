package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.dtos.*
import utn.triponometry.services.TripService
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/trip", produces = [MediaType.APPLICATION_JSON_VALUE])
class TripController(private val tripService: TripService): BaseController() {
    @PostMapping("/optimal-route")
    @ApiOperation("Calculates the optimal route for the given calculator inputs and returns an ID")
    fun calculateOptimalRoute(@RequestBody calculatorInputs: CalculatorInputs, request: HttpServletRequest): ResponseEntity<Any> {
        checkAndGetUserId(request)
        val response = tripService.calculateOptimalRoute(calculatorInputs)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @ApiOperation("Creates a new trip")
    fun createNewTrip(@RequestBody newTripRequest: NewTripRequest, request: HttpServletRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val response = if (newTripRequest.calculatorOutputs == null) {
            tripService.createNewDraft(newTripRequest, userId)
        } else {
            tripService.createNewTrip(newTripRequest, userId)
        }
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
    fun updateTripStatus(@RequestBody tripStatusDto: TripStatusDto, request: HttpServletRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val tripId = ObjectId(tripStatusDto.id)
        val response = tripService.updateTripStatus(userId, tripId, tripStatusDto.newStatus)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/kml/{kmlId}")
    fun getKmlInformation(@PathVariable kmlId: String): ResponseEntity<Any> {
        val response = tripService.getAgendaFromAws(kmlId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/info/{idViaje}")
    @ApiOperation("Returns a specific trip")
    fun getATrip(request: HttpServletRequest,@PathVariable idViaje: String): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val tripObjectId = ObjectId(idViaje)
        val response = tripService.getTrip(userId,tripObjectId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/update")
    @ApiOperation("Update a trip or a draft")
    fun updateDraft(request: HttpServletRequest,@RequestBody tripUpdateDto: TripUpdateDto): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val tripId = ObjectId(tripUpdateDto.id)
        val response = tripService.updateTrip(userId,tripId,tripUpdateDto.trip)
        return ResponseEntity.ok(response)
    }

    @PostMapping("review/{idViaje}")
    @ApiOperation("Creates a new review for a trip")
    fun createNewTrip(request: HttpServletRequest,@PathVariable idViaje: String, @RequestBody review: ReviewRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val tripId = ObjectId(idViaje)
        val response = tripService.addReview(userId,tripId,review)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/explorar/{idViaje}")
    fun getTrip(request: HttpServletRequest, @PathVariable idViaje: String): ResponseEntity<Any> {
        checkAndGetUserId(request)
        val response = tripService.shareTrip(ObjectId(idViaje))
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/delete/{idDraft}")
    fun deleteDraft(request: HttpServletRequest, @PathVariable idDraft: String): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val response = tripService.deleteDraft(userId,ObjectId(idDraft))
        return ResponseEntity.ok(response)
    }

    @PostMapping("/info/activity")
    fun getActivityInfo(request: HttpServletRequest, @RequestBody activity: ActivityRequest): ResponseEntity<Any> {
        checkAndGetUserId(request)
        val response = tripService.getActivityInfo(activity.cityName, activity.activityName)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image")
    fun updateImage(request: HttpServletRequest,  @RequestBody imageReq: ImageRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val tripId = ObjectId(imageReq.id)
        val response = tripService.updateImage(userId,tripId, imageReq.image)
        return ResponseEntity.ok(response)
    }

}