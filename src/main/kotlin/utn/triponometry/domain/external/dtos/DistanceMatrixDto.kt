package utn.triponometry.domain.external.dtos

data class DistanceMatrixResponseDto (
    val destination_addresses: List<String>,
    val origin_addresses: List<String>,
    val rows: List<DistanceRow>,
    val status: String
)

data class DistanceRow (
    val elements: List<Elements>
)
data class Elements (
    val distance: Value,
    val duration: Value,
    val status: String
)
data class Value (
    val text: String,
    val value: Int
)