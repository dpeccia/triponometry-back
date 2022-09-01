package utn.triponometry.domain.dtos

import utn.triponometry.domain.TripStatus

data class TripStatusDto (
    val id: String, val newStatus: TripStatus
)