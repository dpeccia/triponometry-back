package utn.triponometry.domain.dtos

import utn.triponometry.domain.external.dtos.EventDto

data class NewTripRequest (
    val name: String,
    val calculatorInputs: CalculatorInputsDto,
    val calculatorOutputs: CalculatorOutputsDto? = null,
)

data class CalculatorInputsDto(
    val city: CityDto,
    val accomodation: AccomodationDto,
    val activities: List<ActivityDto>,
    val horarios: HorariosDto,
    val mobility: String
)

data class CalculatorOutputsDto(
    val mapId: String,
    val events: List<EventDto>
)

data class CityDto(
    val imageUrl: String,
    val wikiDataId : String,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Float,
    val longitude: Float
)

data class AccomodationDto(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val rate: Int
)

data class ActivityDto(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val rate: Int,
    val wikidata: String,
    val image: String,
    val description: String,
    val wikipediaEnglishLink: String,
    val wikipediaSpanishLink: String
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