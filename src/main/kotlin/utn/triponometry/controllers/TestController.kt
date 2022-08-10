package utn.triponometry.controllers

import com.google.maps.model.TravelMode
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Day
import utn.triponometry.domain.PlaceInput
import utn.triponometry.domain.external.dtos.EventTrip
import utn.triponometry.services.CalendarService
import utn.triponometry.services.GeocodeService
import utn.triponometry.services.TripService

@RestController
@RequestMapping("/test")
class TestController(private val tripService: TripService, private val calendarService: CalendarService, private val geocodeService: GeocodeService) {
    @GetMapping("/coordinates/{place}")
    fun getCurrentWeatherData(@PathVariable place: String): ResponseEntity<Any> {
        val response = geocodeService.getCoordenatesData(place)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/durations/{travelMode}")
    @ApiOperation("Gets the duration between each coordinate")
    fun getDurationsBetweenCoordinates(@PathVariable travelMode: TravelMode, @RequestBody coordinates: List<Coordinates>): ResponseEntity<Any> {
        val places = tripService.getDurationBetween(coordinates.map { PlaceInput(it, 0) }, travelMode)
        return ResponseEntity.ok(places)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/kmlFile/{travelMode}",produces = [MediaType.APPLICATION_XML_VALUE])
    @ApiOperation("Gets the kml file with the recommended route")
    fun getMap(@PathVariable travelMode: TravelMode, @RequestBody days: List<Day>): ResponseEntity<String> {
        val response = tripService.getMapFileData(days,travelMode)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/calendar")
    fun getCalendar(@RequestBody events: List<EventTrip>): ResponseEntity<Any> {
        val response = calendarService.getCalendarData(events)
        return ResponseEntity.ok(response)
    }
}