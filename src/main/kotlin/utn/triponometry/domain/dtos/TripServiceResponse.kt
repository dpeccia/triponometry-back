package utn.triponometry.domain.external.dtos

import utn.triponometry.domain.TripStatus
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.CalculatorInputsDto
import utn.triponometry.domain.dtos.CalculatorOutputsDto

data class TripServiceResponse(
    val daysAmount: Int,
    val kml: String,
    val events: List<EventDto>
)

data class TripServiceRequest(
    val startDate: DateDto,
    val events: List<EventDto>
)

data class EventDto(
    val name: String,
    val start: DateDto,
    val end: DateDto,
)

data class DateDto(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
)

data class EventTime(val name: String, val initTime: Int, val endTime: Int, val duration: Int) {
    fun isBetween(hour: Int) = hour in initTime..endTime
}

data class TripDto(
    val id: String,
    val name: String,
    val calculatorInputs: CalculatorInputsDto,
    val user: User,
    var status: TripStatus,
    val calculatorOutputs: CalculatorOutputsDto? = null
)
