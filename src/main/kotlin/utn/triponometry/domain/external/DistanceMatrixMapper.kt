package utn.triponometry.domain.external

import org.springframework.stereotype.Component
import utn.triponometry.domain.Coordinates
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.dtos.DistanceMatrixResponseDto
import utn.triponometry.domain.external.dtos.Elements

const val mode = "walking"

@Component
class DistanceMatrixAdapter() {

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


    fun matrixToListOfPlaces(matrix: DistanceMatrixResponseDto): List<Place> {
        val places = mutableListOf<Place>()
        matrix.rows.forEachIndexed{ i, _ -> places.add(
            Place(i,matrix.origin_addresses[i],elementsToDistanceMap(i, matrix.rows[i].elements))
        )}
        return places.toList()
    }

    fun elementsToDistanceMap(fatherIndex: Int, elements: List<Elements>): Map<Int,Int> {
        val map = mutableMapOf<Int,Int>()
        elements.forEachIndexed{
                i,e -> if(i != fatherIndex){map[i] = e.duration.value}
        }
        return map.toMap()
    }
}

