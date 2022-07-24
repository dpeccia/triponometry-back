package utn.triponometry.services

import org.springframework.stereotype.Service
import utn.triponometry.domain.external.Geocode
import utn.triponometry.domain.external.OpenWeather

@Service
class GeocodeService(private val geocode: Geocode) {
    fun getCoordenatesData(place: String): Any {
       return geocode.getCoordinates(place)
    }
}