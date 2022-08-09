package utn.triponometry.domain.external

import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import utn.triponometry.domain.external.dtos.CalendarDto
import utn.triponometry.domain.external.dtos.DateDto
import utn.triponometry.domain.external.dtos.EventTrip
import utn.triponometry.domain.external.dtos.EventsDto
import java.io.FileOutputStream
import java.util.*

class CalendarAdapter {

    fun createCalendar(events : List<EventTrip>): Calendar {

        val calendar = Calendar()
        calendar.properties.add(ProdId("-//Events Calendar//iCal4j 1.0//EN"))
        calendar.properties.add(Version.VERSION_2_0)
        calendar.properties.add(CalScale.GREGORIAN)

        events.map {events -> createTimeEvent(events)}.forEach { e -> calendar.components.add(e) }
        return calendar
    }

    fun getCalendarResponse(events : List<EventTrip>): CalendarDto {
       val eventsDto = events.map { events -> mapEventsToEventsDto(events) }
       return CalendarDto(createCalendar(events).toString(),eventsDto)
    }
    fun mapEventsToEventsDto(e: EventTrip): EventsDto {

        //El Calendar es necesario para calcular la hora y minutos finales
        val cal: java.util.Calendar = GregorianCalendar()
        cal[java.util.Calendar.HOUR_OF_DAY] = e.startDate.hour
        cal[java.util.Calendar.MINUTE] = e.startDate.minute+e.duration
        val dateTime = DateTime(cal.time)

        return EventsDto(
            e.name,
            DateDto(e.startDate.year, e.startDate.month, e.startDate.day, e.startDate.hour, e.startDate.minute),
            DateDto(e.startDate.year, e.startDate.month, e.startDate.day, dateTime.hours, dateTime.minutes)
        )
    }


    fun createIcsFile(calendar: Calendar,fileName: String){
        val fout = FileOutputStream(fileName)
        val outputter = CalendarOutputter()
        outputter.output(calendar, fout)
    }

    fun createTimeEvent(eventTrip: EventTrip): VEvent {
        val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
        val timezone = registry.getTimeZone("America/Buenos_Aires")
        val tz = timezone.vTimeZone
        val e = eventTrip.startDate
        val start = createDateTime(e.day,e.month,e.year,e.hour, e.minute)
        val end = createDateTime(e.day,e.month,e.year,e.hour, e.minute+eventTrip.duration)
        val event = VEvent(start, end, eventTrip.name)
        event.properties.add(tz.timeZoneId)
        return event
    }

    fun createDateTime(day: Int, month: Int,year: Int,hour: Int, minute: Int): DateTime {
        val startDate: java.util.Calendar = GregorianCalendar()
        startDate[java.util.Calendar.MONTH] = month
        startDate[java.util.Calendar.DAY_OF_MONTH] = day
        startDate[java.util.Calendar.YEAR] = year
        startDate[java.util.Calendar.HOUR_OF_DAY] = hour
        startDate[java.util.Calendar.MINUTE] = minute
        startDate[java.util.Calendar.SECOND] = 0
        return DateTime(startDate.time)
    }

}