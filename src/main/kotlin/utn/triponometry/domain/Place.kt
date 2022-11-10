package utn.triponometry.domain

data class Place(
    val id: Int,
    val name: String,
    val durations: Map<Int, Int>, // id of other place & duration in minutes to the other place
    var timeSpent: Int? = null, // minutes
    val coordinates: Coordinates? = null
)