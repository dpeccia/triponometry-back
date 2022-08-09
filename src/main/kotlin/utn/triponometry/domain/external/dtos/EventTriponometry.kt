package utn.triponometry.domain.external.dtos

data class EventTrip(
    val name: String,
    val startDate : DateDto,
    val duration: Int
)

data class CalendarDto (
    val rawCalendar: String,
    val events: List<EventsDto>,
)

data class EventsDto (
    val name: String,
    val start: DateDto,
    val end: DateDto,
)

data class DateDto (
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
)

