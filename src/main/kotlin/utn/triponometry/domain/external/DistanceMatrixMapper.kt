package utn.triponometry.domain.external

import org.springframework.stereotype.Component
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Place
import utn.triponometry.domain.PlaceInput
import utn.triponometry.domain.external.dtos.DistanceMatrixResponseDto
import utn.triponometry.domain.external.dtos.Elements
import utn.triponometry.helpers.GoogleDistanceMatrixApiException

@Component
class DistanceMatrixAdapter {
    //Input example: "-34.5993652, -58.5122799"
    fun parseReq(coord: Coordinates): String{
        return "${coord.latitude},${coord.longitude}"
    }

    fun mapArrayToString(arrayList: List<Coordinates>): String {
        var concatString = ""
        for (item: Coordinates in arrayList){
            concatString += parseReq(item) + "|"
        }
        return concatString.dropLast(1)
    }

    fun matrixToListOfPlaces(matrix: DistanceMatrixResponseDto, placesInputs: List<PlaceInput>): List<Place> {
        val places = mutableListOf<Place>()
        validateMatrix(matrix)
        matrix.rows!!.forEachIndexed { i, _ -> places.add(
            Place(i, placesInputs[i].name, elementsToDistanceMap(i, matrix.rows[i].elements!!), placesInputs[i].timeSpent, placesInputs[i].coordinates)
        )}
        return places.toList()
    }

    fun validateMatrix(matrix: DistanceMatrixResponseDto) {
        matrix.rows!!.forEach { r ->
            r.elements!!.forEach { e ->
                validateStatus(e.status!!)
            }
        }
    }

    fun validateStatus(status: String){
        when (status){
            "NOT_FOUND" -> throw GoogleDistanceMatrixApiException("No se pudo generar el recorrido óptimo: no se puede encontrar en el mapa una localidad")
            "ZERO_RESULTS" -> throw GoogleDistanceMatrixApiException("No se pudo generar el recorrido óptimo: no existe una ruta entre los destinos")
            "MAX_ROUTE_LENGHT_EXCEEDED" -> throw GoogleDistanceMatrixApiException("No se pudo generar el recorrido óptimo: la ruta es muy larga para procesar")
        }
    }

    fun elementsToDistanceMap(fatherIndex: Int, elements: List<Elements>): Map<Int,Int> {
        val map = mutableMapOf<Int,Int>()
        elements.forEachIndexed { i,e ->
            if(i != fatherIndex)
                map[i] = e.duration!!.value!! / 60
        }
        return map.toMap()
    }
}

