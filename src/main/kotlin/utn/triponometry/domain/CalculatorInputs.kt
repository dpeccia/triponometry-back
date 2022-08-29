package utn.triponometry.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.maps.model.TravelMode
import java.time.LocalTime

data class CalculatorInputs(
    val travelMode: TravelMode,
    val places: List<PlaceInput>,
    val time: TimeInput
)

data class PlaceInput (
    val name: String,
    val coordinates: Coordinates,
    val timeSpent: Int
)

data class TimeInput (
    val startHour: String,
    val finishHour: String,
    val breakfast: Int,
    val lunch: Int,
    val snack: Int,
    val dinner: Int,
    val freeDays: Int
) {
    @JsonIgnore
    val startTime = LocalTime.parse(startHour)
    @JsonIgnore
    val finishTime = LocalTime.parse(finishHour)
}