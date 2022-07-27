package utn.triponometry.domain.external

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.TravelMode
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.dtos.DistanceMatrixResponseDto
import utn.triponometry.helpers.GoogleDistanceMatrixApiException
import utn.triponometry.helpers.GoogleGeocodeApiException
import utn.triponometry.properties.TriponometryProperties
import java.util.*

@Component
@EnableAutoConfiguration
class GoogleApi (triponometryProperties: TriponometryProperties) {
    val apiKey = triponometryProperties.google.apiKey
    val baseUrl = triponometryProperties.distance.url
    var matrixAdapter = DistanceMatrixAdapter()

    fun buildContext(): GeoApiContext? {
        return GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()
    }

    fun shutdown(context: GeoApiContext) {
        context.shutdown()
    }

    fun getCoordinatesFromGeocodeApi(place: String): String? {
        try {
            val context = buildContext()
            val results = GeocodingApi.geocode(context, place).await()
            shutdown(context!!)

            val gson = Gson()
            return gson.toJson(results[0])

        } catch (e: Exception) {
            throw GoogleGeocodeApiException("There was an error with the Geocoding Server")
        }
    }

    fun getListOfPlaces(places: List<Coordinates>, travelMode: TravelMode): List<Place> {
        val distancesMatrix = getDistanceMatrix(places, places, travelMode)
        return matrixAdapter.matrixToListOfPlaces(distancesMatrix!!)
    }

    fun getDistanceMatrix(origins: List<Coordinates>, destinations: List<Coordinates>, travelMode: TravelMode): DistanceMatrixResponseDto? {
        val orArray = matrixAdapter.mapArrayToString(origins)
        val desArray = matrixAdapter.mapArrayToString(destinations)
        val results = getMatrix(baseUrl, orArray, desArray, travelMode)
        return jacksonObjectMapper().readerFor(DistanceMatrixResponseDto::class.java).readValue(results)
    }

    fun getMatrix(baseUrl: String, origins: String, destinations: String, mode: TravelMode): String {
        val travelMode = mode.name.lowercase(Locale.getDefault())
        val endpoint = "?destinations=${destinations}&origins=${origins}&mode=$travelMode&sensor=false"
        return getFromDistanceMatrixApi(baseUrl, endpoint)
    }

   private fun getFromDistanceMatrixApi(baseUrl: String, endpoint: String) =
        WebClient.create(baseUrl).get()
            .uri("$endpoint&key=$apiKey")
            .retrieve()
            .onStatus(HttpStatus::isError) {
                val statusCode = it.statusCode()
                throw GoogleDistanceMatrixApiException("${statusCode.value()} - ${statusCode.reasonPhrase}")
            }
            .bodyToMono(String::class.java)
            .block() ?: throw GoogleDistanceMatrixApiException("There was an error with the Distance Matrix Server")
}
