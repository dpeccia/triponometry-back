package utn.triponometry.domain

data class CalculatorInputs (
    val daysRestriction: Int, // TODO: tener en cuenta esto
    val freeDays: Int, // TODO: tener en cuenta esto
    val timePerDay: Int, // minutes
    val places: List<Place>
)