package utn.triponometry.services

import org.springframework.stereotype.Service
import utn.triponometry.domain.external.CalendarAdapter
import utn.triponometry.domain.external.dtos.CalendarDto
import utn.triponometry.domain.external.dtos.EventTrip

@Service
class CalendarService() {
    fun getCalendarData(events: List<EventTrip>): CalendarDto {
       return CalendarAdapter().getCalendarResponse(events)
    }
}