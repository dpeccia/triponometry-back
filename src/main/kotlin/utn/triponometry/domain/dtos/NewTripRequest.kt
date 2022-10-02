package utn.triponometry.domain.dtos

import utn.triponometry.domain.external.dtos.EventDto

data class NewTripRequest (
    var name: String,
    val calculatorInputs: CalculatorInputsDto,
    var calculatorOutputs: CalculatorOutputsDto? = null,
)

data class CalculatorInputsDto(
    val city: CityDto,
    val accommodation: AccomodationDto? = null,
    val activities: List<ActivityDto> ? = null,
    val horarios: HorariosDto ? = null,
    val mobility: String ? = null
)

data class CalculatorOutputsDto(
    val mapId: String,
    val events: List<EventDto>,
    val daysAmount: Int
)

data class CityDto(
    var imageUrl: String,
    val wikiDataId : String? = null,
    val name: String,
    val region: String? = null,
    val country: String,
    val latitude: Float,
    val longitude: Float
)

data class AccomodationDto(
    val name: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val rate: Int? = null
)

data class ActivityDto(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val rate: Int? = null,
    val wikidata: String? = null,
    val image: String? = null,
    val description: String? = null,
    val wikipediaEnglishLink: String? = null,
    val wikipediaSpanishLink: String? = null,
    val timeSpent: Int? = null
)

data class HorariosDto(
    val desayuno: TimeDto ? = null,
    val merienda: TimeDto ? = null,
    val almuerzo: TimeDto ? = null,
    val cena: TimeDto ? = null,
    val despertarse: String ? = null,
    val dormirse: String ? = null,
    val libres: String ? = null
)

data class TimeDto(
    val time: String,
    val number: String
)