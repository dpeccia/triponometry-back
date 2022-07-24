package utn.triponometry.services

import org.springframework.stereotype.Service
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.DistanceMatrixAdapter
import utn.triponometry.domain.external.GoogleApi

@Service
class DistanceMatrixService(private val googleApi: GoogleApi) {
    fun getDistanceMatrixData(coordinates: List<Coordinates>): List<Place> {
       return googleApi.getListOfPlaces(coordinates)
    }
}