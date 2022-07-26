package utn.triponometry.domain.external

import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import utn.triponometry.domain.external.dtos.EventTrip
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

    fun calendarAsString(events : List<EventTrip>): String{
       return createCalendar(events).toString()
    }

    fun createIcsFile(calendar: Calendar,fileName: String){
        val fout = FileOutputStream(fileName)
        val outputter = CalendarOutputter()
        outputter.output(calendar, fout)
    }

    fun createTimeEvent(e: EventTrip): VEvent {
        val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
        val timezone = registry.getTimeZone("America/Buenos_Aires")
        val tz = timezone.vTimeZone

        val start = createDateTime(e.day,e.month,e.year,e.hour, e.minute)
        val ending = e.hour + e.duration
        val end = createDateTime(e.day,e.month,e.year,ending, e.minute)
        val event = VEvent(start, end, e.name)
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