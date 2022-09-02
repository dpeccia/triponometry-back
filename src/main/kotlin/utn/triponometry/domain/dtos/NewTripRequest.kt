package utn.triponometry.domain.dtos

import utn.triponometry.domain.external.dtos.EventDto

data class NewTripRequest (
    var name: String,
    val calculatorInputs: CalculatorInputsDto,
    var calculatorOutputs: CalculatorOutputsDto? = null,
)

data class CalculatorInputsDto(
    val city: CityDto,
    val accommodation: AccomodationDto,
    val activities: List<ActivityDto>,
    val horarios: HorariosDto,
    val mobility: String
)

data class CalculatorOutputsDto(
    val mapId: String,
    val events: List<EventDto>,
    val daysAmount: Int
)

data class CityDto(
    val imageUrl: String,
    val wikiDataId : String? = null,
    val name: String,
    val region: String? = null,
    val country: String,
    val latitude: Float,
    val longitude: Float
)

data class AccomodationDto(
    val name: String,
    val latitude: Float,
    val longitude: Float,
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
    val desayuno: TimeDto,
    val merienda: TimeDto,
    val almuerzo: TimeDto,
    val cena: TimeDto,
    val despertarse: String,
    val dormirse: String,
    val libres: String
)

data class TimeDto(
    val time: String,
    val number: String
)