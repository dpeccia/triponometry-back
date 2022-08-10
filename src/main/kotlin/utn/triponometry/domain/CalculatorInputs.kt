package utn.triponometry.domain

import com.google.maps.model.TravelMode

data class CalculatorInputs (
    val daysRestriction: Int, // TODO: tener en cuenta esto
    val freeDays: Int, // TODO: tener en cuenta esto
    val timePerDay: Int, // minutes
    val travelMode: TravelMode,
    val places: List<PlaceInput>
)

data class PlaceInput (
    val coordinates: Coordinates,
    val timeSpent: Int
)