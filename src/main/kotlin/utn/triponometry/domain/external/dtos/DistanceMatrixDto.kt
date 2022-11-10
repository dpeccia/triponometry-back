package utn.triponometry.domain.external.dtos

data class DistanceMatrixResponseDto (
    val destination_addresses: List<String>? = null,
    val origin_addresses: List<String>? = null,
    val rows: List<DistanceRow>? = null,
    val status: String? = null,
    val available_travel_modes: List<String>? = null
)

data class DistanceRow (
    val elements: List<Elements>? = null
)
data class Elements (
    val distance: Value? = null,
    val duration: Value? = null,
    val status: String? = null
)
data class Value (
    val text: String? = null,
    val value: Int? = null
)

data class StatusDto (
    val status: String? = null
)