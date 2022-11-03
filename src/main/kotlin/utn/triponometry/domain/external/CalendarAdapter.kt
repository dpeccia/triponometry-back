package utn.triponometry.domain.external

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.Version
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.dtos.DateDto
import utn.triponometry.domain.external.dtos.EventDto
import utn.triponometry.domain.external.dtos.EventTime
import java.time.Duration
import java.time.LocalTime
import java.util.*


class CalendarAdapter() {

    //FUNCTIONS TO CREATE EVENTS:
    fun getListOfEvents(days: List<Day>, inputs: CalculatorInputs): MutableList<EventDto> {
        val events = mutableListOf<EventDto>()
        var cal = GregorianCalendar()

        days.forEach{d -> events.addAll(mapDayEventstoEventsDto(d, inputs, cal))}

        val timeInputs = inputs.time
        repeat(timeInputs.freeDays){
            addFreeDay(cal, events, timeInputs.startTime, timeInputs.finishTime)
        }
        return events
    }

    fun getTravelTime(place: Place, id: Int): Int {
        return place.durations[id]!!
    }

    fun mapDayEventstoEventsDto(day: Day, inputs: CalculatorInputs, cal: GregorianCalendar): MutableList<EventDto> {
        val timeInputs = inputs.time
        cal[java.util.Calendar.HOUR_OF_DAY] = timeInputs.startTime.hour
        cal[java.util.Calendar.MINUTE] = timeInputs.startTime.minute

        val events = mutableListOf<EventDto>()
        val defaultEvents = makeListOfDefaultEvents(inputs)

        for ((index, activity) in day.route.withIndex()) {
            addDefaultEvent(cal, events, defaultEvents)
            if (index > 0) {
                val travelTime = getTravelTime(day.route[index-1],activity.id)
                cal.add(GregorianCalendar.MINUTE, travelTime)
                events.add(createEventDto(activity.name, cal, GregorianCalendar.MINUTE, activity.timeSpent!!))
                cal.add(GregorianCalendar.MINUTE, activity.timeSpent)
            }
        }

        while (defaultEvents.isNotEmpty()) {
            cal.add(GregorianCalendar.MINUTE, 60)
            cal[java.util.Calendar.MINUTE] = 0
            addDefaultEvent(cal, events, defaultEvents)
        }
        //Aumento el día para los siguientes eventos
        cal.add(GregorianCalendar.DAY_OF_MONTH, 1)
        return events
    }

    fun addDefaultEvent(cal: GregorianCalendar, events: MutableList<EventDto>, defaultEvents: MutableList<EventTime>) {
        if (defaultEvents.isNotEmpty()) {
            var nextEvent = defaultEvents[0]
            if (nextEvent.isBetween(cal.get(GregorianCalendar.HOUR_OF_DAY))) {
                events.add(createEventDto(nextEvent.name, cal, GregorianCalendar.MINUTE, nextEvent.duration))
                defaultEvents.removeFirst()
                cal.add(GregorianCalendar.MINUTE, nextEvent.duration)
            }
        }
    }

    fun addFreeDay(cal: GregorianCalendar, events: MutableList<EventDto>, start: LocalTime, finish: LocalTime) {
        cal[java.util.Calendar.HOUR_OF_DAY] = start.hour
        cal[java.util.Calendar.MINUTE] = start.minute
        events.add(createEventDto("Dia Libre", cal, GregorianCalendar.MINUTE, Duration.between(start, finish).toMinutes().toInt()))
        cal.add(GregorianCalendar.DAY_OF_MONTH, 1)
    }

    fun makeListOfDefaultEvents(inputs: CalculatorInputs): MutableList<EventTime> {
        val timeInputs = inputs.time
        var defaultEvents = mutableListOf<EventTime>()
        if (timeInputs.breakfast != 0) defaultEvents.add(EventTime("Desayuno", 7, 11, timeInputs.breakfast))
        if (timeInputs.lunch != 0) defaultEvents.add(EventTime("Almuerzo", 11, 16, timeInputs.lunch))
        if (timeInputs.snack != 0) defaultEvents.add(EventTime("Merienda", 16, 19, timeInputs.snack))
        if (timeInputs.dinner != 0) defaultEvents.add(EventTime("Cena", 19, 22, timeInputs.dinner))
        return defaultEvents
    }

    //FUNCTIONS TO ICS:
    fun createEventDto(name: String, cal: GregorianCalendar, type: Int, duration: Int): EventDto {
        val cal2 = cal.clone() as GregorianCalendar
        cal2.add(type, duration)
        return EventDto(
            name,
            mapCalendarToDateDto(cal),
            mapCalendarToDateDto(cal2)
        )
    }

    fun createTimeEvent(eventTrip: EventDto): VEvent {
        val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
        val timezone = registry.getTimeZone("America/Buenos_Aires") //TODO: deberia chequearse la zona?
        val tz = timezone.vTimeZone
        val startTime = eventTrip.start
        val endTime = eventTrip.end
        val start = createDateTime(startTime.day, startTime.month, startTime.year, startTime.hour, startTime.minute)
        val end = createDateTime(endTime.day, endTime.month, endTime.year, endTime.hour, endTime.minute)
        val event = VEvent(start, end, eventTrip.name)
        event.properties.add(tz.timeZoneId)
        return event
    }

    fun createDateTime(day: Int, month: Int, year: Int, hour: Int, minute: Int): DateTime {
        val startDate: java.util.Calendar = GregorianCalendar()
        startDate[java.util.Calendar.MONTH] = month
        startDate[java.util.Calendar.DAY_OF_MONTH] = day
        startDate[java.util.Calendar.YEAR] = year
        startDate[java.util.Calendar.HOUR_OF_DAY] = hour
        startDate[java.util.Calendar.MINUTE] = minute
        startDate[java.util.Calendar.SECOND] = 0
        return DateTime(startDate.time)
    }

    fun createCalendar(events: List<EventDto>, startDate: DateDto): Calendar {
        val calendar = Calendar()
        calendar.properties.add(ProdId("-//Events Calendar//iCal4j 1.0//EN"))
        calendar.properties.add(Version.VERSION_2_0)
        calendar.properties.add(CalScale.GREGORIAN)

        val updatedEvents = mapEvents(events, startDate)
        updatedEvents.map { events -> createTimeEvent(events) }.forEach { e -> calendar.components.add(e) }
        return calendar
    }

    fun mapEvents(events: List<EventDto>, start: DateDto): MutableList<EventDto> {

        val response = mutableListOf<EventDto>()
        val eventFirstDate = mapDateDtoToCalendar(events[0].start)
        val beginning = mapDateDtoToCalendar(start)
        val calc = beginning.timeInMillis - eventFirstDate.timeInMillis
        val difDays = (calc / (24 * 60 * 60 * 1000)).toInt();

        for (event in events) {
            val start = mapDateDtoToCalendar(event.start)
            start.add(GregorianCalendar.DAY_OF_MONTH, difDays)
            val end = mapDateDtoToCalendar(event.end)
            end.add(GregorianCalendar.DAY_OF_MONTH, difDays)
            response.add(
                EventDto(
                    event.name,
                    mapCalendarToDateDto(start),
                    mapCalendarToDateDto(end),
                )
            )

        }
        return response

    }

    private fun mapCalendarToDateDto(cal: GregorianCalendar): DateDto {
        return DateDto(
            cal[java.util.Calendar.YEAR],
            cal[java.util.Calendar.MONTH],
            cal[java.util.Calendar.DAY_OF_MONTH],
            cal[java.util.Calendar.HOUR_OF_DAY],
            cal[java.util.Calendar.MINUTE],
        )
    }

    private fun mapDateDtoToCalendar(date: DateDto): GregorianCalendar {
        var cal = GregorianCalendar()
        cal[java.util.Calendar.YEAR] = date.year
        cal[java.util.Calendar.MONTH] = date.month
        cal[java.util.Calendar.DAY_OF_MONTH] = date.day
        cal[java.util.Calendar.HOUR_OF_DAY] = date.hour
        cal[java.util.Calendar.MINUTE] = date.minute
        return cal
    }

}