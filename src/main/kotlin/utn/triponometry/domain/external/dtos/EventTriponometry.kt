package utn.triponometry.domain.external.dtos

data class EventTrip(
    val name: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int,
    val duration: Int
)