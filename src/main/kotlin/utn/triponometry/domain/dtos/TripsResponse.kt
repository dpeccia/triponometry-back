package utn.triponometry.domain.dtos

import utn.triponometry.domain.external.dtos.TripDto

data class TripsResponse(
    val active: List<TripDto>,
    val archived: List<TripDto>,
    val draft: List<TripDto>,
)