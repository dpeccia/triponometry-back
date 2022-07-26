package utn.triponometry.services

import net.fortuna.ical4j.model.Calendar
import org.springframework.stereotype.Service
import utn.triponometry.domain.external.CalendarAdapter
import utn.triponometry.domain.external.dtos.EventTrip

@Service
class CalendarService() {
    fun getCalendarData(events: List<EventTrip>): String {
       return CalendarAdapter().calendarAsString(events)
    }
}