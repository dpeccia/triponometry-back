package utn.triponometry.services

import org.springframework.stereotype.Service
import utn.triponometry.domain.external.OpenWeather

@Service
class WeatherService(private val openWeather: OpenWeather) {
    fun getCurrentWeatherData(city: String): Any {
        val coordinates = openWeather.getCoordinates(city)
        return openWeather.getCurrentWeather(city, coordinates)
    }
}