package utn.triponometry.services

import net.fortuna.ical4j.model.Calendar
import org.springframework.stereotype.Service
import utn.triponometry.domain.external.CalendarAdapter
import utn.triponometry.domain.external.dtos.EventDto
import utn.triponometry.domain.external.dtos.TripServiceRequest

@Service
class CalendarService() {
    fun getCalendarData(events: TripServiceRequest): String {
       return CalendarAdapter().createCalendar(events.events,events.startDate).toString()
    }
}