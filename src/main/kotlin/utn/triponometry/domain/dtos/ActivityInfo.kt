package utn.triponometry.domain.dtos

data class ActivityRequest (
    val cityName: String,
    val activityName: String
)

data class ActivityInfoResponse (
    val tripQty: Int,
    val minTimeSpent: Int,
    val maxTimeSpent: Int
)