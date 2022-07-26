package utn.triponometry.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.domain.external.dtos.EventTrip
import utn.triponometry.services.CalendarService

@RestController
@RequestMapping("/calendar")
class CalendarController(private val calendarService: CalendarService) {
    @PostMapping("/file")
    fun getCalendar(@RequestBody events: List<EventTrip>): ResponseEntity<Any> {
        val response = calendarService.getCalendarData(events)
        return ResponseEntity.ok(response)
    }
}