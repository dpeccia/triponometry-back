package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.external.dtos.TripServiceRequest
import utn.triponometry.services.CalendarService
import utn.triponometry.services.TripService
import java.lang.RuntimeException

@RestController
@RequestMapping("/calendar")
class CalendarController(private val calendarService: CalendarService) {
    @PostMapping("/rawContent")
    @ApiOperation("Returns the content of an ics file")
    fun getCalendar(@RequestBody events: TripServiceRequest): ResponseEntity<Any> {
        val response = calendarService.getCalendarData(events)
        return ResponseEntity.ok(response)
    }
}