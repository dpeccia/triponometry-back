package utn.triponometry.domain.external

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.GeocodingResult
import org.springframework.stereotype.Component
import utn.triponometry.domain.Coordinates
import utn.triponometry.helpers.GoogleGeocodeApiException
import utn.triponometry.helpers.OpenWeatherException
import utn.triponometry.properties.TriponometryProperties

@Component
class Geocode(triponometryProperties: TriponometryProperties, private val googleApi: GoogleApi) {
    private val apiKey = triponometryProperties.google.apiKey

    fun getCoordinates(place: String): Coordinates {
        val results = googleApi.getCoordinatesFromGeocodeApi(place)
        val gson = Gson()
        try{
            var apiResponse = gson.fromJson(results, GeocodingResult::class.java)
            val latitude = apiResponse.geometry.location.lat
            val longitude = apiResponse.geometry.location.lng
            return Coordinates(latitude, longitude)
        }catch (e: Exception){
            throw GoogleGeocodeApiException("No results were found")
        }
    }

}

