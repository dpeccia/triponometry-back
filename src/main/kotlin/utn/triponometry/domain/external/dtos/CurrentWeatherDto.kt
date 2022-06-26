package utn.triponometry.domain.external.dtos

data class CurrentWeatherDto (
    val weather: List<WeatherDto>,
    val main: MainDto
)

data class WeatherDto (
    val main: String,
    val description: String
)

data class MainDto (
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double
)