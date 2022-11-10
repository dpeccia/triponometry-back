package utn.triponometry.domain.external

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.maps.model.TravelMode
import io.mockk.mockk
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.ComponentList
import net.fortuna.ical4j.model.component.VEvent
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place
import utn.triponometry.domain.TimeInput
import utn.triponometry.domain.external.dtos.DateDto
import utn.triponometry.domain.external.dtos.TripServiceRequest
import utn.triponometry.helpers.IllegalTripException
import utn.triponometry.properties.Distance
import utn.triponometry.properties.Google
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.repos.TripRepository
import utn.triponometry.repos.UserRepository
import utn.triponometry.services.CalendarService
import utn.triponometry.services.TripService
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalendarTest {
    private val hotel = Place(0, "Hotel", mapOf(1 to 30, 2 to 60, 3 to 35),0)
    private val colloseum = Place(1, "Colloseum", mapOf(0 to 25, 2 to 20, 3 to 25), 90)
    private val fontanaDiTrevi = Place(2, "Fontana Di Trevi", mapOf(0 to 50, 1 to 20, 3 to 25), 60)
    private val vaticano = Place(3, "Vaticano", mapOf(0 to 40, 1 to 30, 2 to 30), 120)

    val userRepository: UserRepository = mockk()
    val tripRepository: TripRepository = mockk()
    val triponometryProperties = TriponometryProperties(
        distance = Distance("https://maps.googleapis.com/maps/api/distancematrix/json"),
        google = Google("AIzaSyDYvqEW16HdPdIRXCncIIGtjQ-4bbQk0i0")
    )
    var tripService = TripService(
        triponometryProperties,tripRepository,userRepository, GoogleApi(triponometryProperties)
    )
    var calendarService = CalendarService()



    @Test
    fun `1 day, 2 activities, 4 meals`() {
        val timeInput = TimeInput("09:00", "23:00", 30,60,20,60, 0)
        val inputs = CalculatorInputs(TravelMode.WALKING, listOf(), timeInput)
        
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val days = listOf(day1)
        
        val events = CalendarAdapter().getListOfEvents(days, inputs)
        Assertions.assertEquals(6,events.size)
    }

    @Test
    fun `1 day, 4 activities, 3 meals`() {
        val timeInput = TimeInput("09:00", "23:00", 30,60,0,60, 0)
        val inputs = CalculatorInputs(TravelMode.WALKING, listOf(), timeInput)

        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi,vaticano,colloseum))

        val days = listOf(day1)
        val events = CalendarAdapter().getListOfEvents(days, inputs)
        Assertions.assertEquals(7,events.size)
    }

    @Test
    fun `2 days, 4 activities, 3 meals`() {
        val timeInput = TimeInput("09:00", "23:00", 30,60,0,60, 0)
        val inputs = CalculatorInputs(TravelMode.WALKING, listOf(), timeInput)

        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))

        val days = listOf(day1,day2)
        val events = CalendarAdapter().getListOfEvents(days, inputs)

        //Debería haber 10 eventos porque tenemos 4 eventos turisticos, y 3 comidas por DIA
        Assertions.assertEquals(10,events.size)
    }

    @Test
    fun `3 days, 6 activities, 3 meals`() {
        val timeInput = TimeInput("09:00", "23:00", 30,60,0,60, 0)
        val inputs = CalculatorInputs(TravelMode.WALKING, listOf(), timeInput)

        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))
        val day3 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))

        val days = listOf(day1,day2,day3)
        val events = CalendarAdapter().getListOfEvents(days, inputs)

        Assertions.assertEquals(15,events.size)
    }

    @Test
    fun `3 days, 6 activities, 3 meals with 2 free Days`() {
        val timeInput = TimeInput("09:00", "23:00", 30,60,0,60, 2)
        val inputs = CalculatorInputs(TravelMode.WALKING, listOf(), timeInput)

        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))
        val day3 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))

        val days = listOf(day1,day2,day3)
        val events = CalendarAdapter().getListOfEvents(days, inputs)

        Assertions.assertEquals(17,events.size)
    }

    @Test
    fun `valid ics`(){
        val file = File("src/test/resources/calendar.ics")
        val content : InputStream = file.inputStream()

        val builder = CalendarBuilder()
        val calendar = builder.build(content)

        Assertions.assertEquals(17, calendar.components.size)
    }

    @Test
    fun `valid ics generated`(){
        val calendar = CalendarAdapter()
        val timeInput = TimeInput("09:00", "23:00", 30,60,0,60, 2)
        val inputs = CalculatorInputs(TravelMode.WALKING, listOf(), timeInput)

        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))

        val days = listOf(day1,day2)
        val events = calendar.getListOfEvents(days, inputs)

        val calendarIcs = calendar.createCalendar(events,
        DateDto(2022,7,29,9,0)
        )

        Assertions.assertEquals(12,calendarIcs.components.size)
        Assertions.assertEquals("Desayuno",calendarIcs.components[0].properties[3].value)
        Assertions.assertEquals("Día Libre",calendarIcs.components.last().properties[3].value)
    }


    @Test
    fun `lima - actividad con 15 horas de duracion`() {

        val request = "{\"places\":[{\"name\":\"hotel plaza mayor\",\"coordinates\":{\"latitude\":-12.047585,\"longitude\":-77.031105},\"timeSpent\":null},{\"name\":\"Cathedral of Lima\",\"coordinates\":{\"latitude\":-12.046372,\"longitude\":-77.02984},\"timeSpent\":180},{\"name\":\"Huaca Pucllana\",\"coordinates\":{\"latitude\":-12.110232,\"longitude\":-77.03212},\"timeSpent\":120},{\"name\":\"San Cristobal Hill\",\"coordinates\":{\"latitude\":-12.034643,\"longitude\":-77.017685},\"timeSpent\":180},{\"name\":\"Casa O'Higgins\",\"coordinates\":{\"latitude\":-12.047599,\"longitude\":-77.032555},\"timeSpent\":120},{\"name\":\"Fountain at Plaza Mayor (Lima)\",\"coordinates\":{\"latitude\":-12.045972,\"longitude\":-77.03059},\"timeSpent\":180},{\"name\":\"Basilica of Nuestra Señora de la Merced\",\"coordinates\":{\"latitude\":-12.0483,\"longitude\":-77.0327},\"timeSpent\":900}],\"travelMode\":\"WALKING\",\"time\":{\"startHour\":\"08:30\",\"finishHour\":\"23:59\",\"breakfast\":null,\"lunch\":\"80\",\"snack\":null,\"dinner\":60,\"freeDays\":\"1\"}}"
        val calculatorInputs: CalculatorInputs =
            jacksonObjectMapper().readerFor(CalculatorInputs::class.java).readValue(request)
        val exception = assertThrows<IllegalTripException> {
            tripService.calculateOptimalRoute(calculatorInputs)
        }
        Assertions.assertEquals("La duración de la actividad Basilica of Nuestra Señora de la Merced sobrepasa los límites del día", exception.message)
    }

    @Test
    fun `barcelona - tiempo de comidas muy extenso`() {

        var request = "{\"places\":[{\"name\":\"ramblas apartments\",\"coordinates\":{\"latitude\":41.37946,\"longitude\":2.1750386},\"timeSpent\":null},{\"name\":\"Royal Square\",\"coordinates\":{\"latitude\":41.380093,\"longitude\":2.1750157},\"timeSpent\":60},{\"name\":\"Font de les Tres Gràcies\",\"coordinates\":{\"latitude\":41.380093,\"longitude\":2.1752775},\"timeSpent\":30},{\"name\":\"Lampadaire de la place Royale\",\"coordinates\":{\"latitude\":41.3802,\"longitude\":2.17515},\"timeSpent\":120}],\"travelMode\":\"WALKING\",\"time\":{\"startHour\":\"08:00\",\"finishHour\":\"23:00\",\"breakfast\":540,\"lunch\":840,\"snack\":null,\"dinner\":1260,\"freeDays\":\"2\"}}"
        val calculatorInputs: CalculatorInputs =
            jacksonObjectMapper().readerFor(CalculatorInputs::class.java).readValue(request)
        val exception = assertThrows<IllegalTripException> {
            tripService.calculateOptimalRoute(calculatorInputs)
        }
        Assertions.assertEquals("La duración máxima de las comidas es de 4 horas", exception.message)
    }


}

