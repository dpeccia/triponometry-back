package utn.triponometry.domain.external

import com.google.maps.model.TravelMode
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.ComponentList
import net.fortuna.ical4j.model.component.VEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utn.triponometry.domain.CalculatorInputs
import utn.triponometry.domain.Day
import utn.triponometry.domain.Place
import utn.triponometry.domain.external.dtos.DateDto
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalendarTest {
    private val hotel = Place(0, "Hotel", mapOf(1 to 30, 2 to 60, 3 to 35),0)
    private val colloseum = Place(1, "Colloseum", mapOf(0 to 25, 2 to 20, 3 to 25), 90)
    private val fontanaDiTrevi = Place(2, "Fontana Di Trevi", mapOf(0 to 50, 1 to 20, 3 to 25), 185)
    private val vaticano = Place(3, "Vaticano", mapOf(0 to 40, 1 to 30, 2 to 30), 125)
    
    @Test
    fun `1 day, 2 activities, 4 meals`() {
        val inputs = CalculatorInputs(3,0,100,TravelMode.WALKING,
            listOf(),9,30,60,20,60)
        
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val days = listOf(day1)
        
        val events = CalendarAdapter().getListOfEvents(days,inputs)
        Assertions.assertEquals(6,events.size)
    }

    @Test
    fun `1 day, 4 activities, 2 meals`() {
        val inputs = CalculatorInputs(3,0,100,TravelMode.WALKING,
            listOf(),9,30,60,0,0)
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi,vaticano,colloseum))

        val days = listOf(day1)
        val events = CalendarAdapter().getListOfEvents(days,inputs)
        
        //Debería haber 10 eventos porque tenemos 4 eventos turisticos, y 3 comidas por DIA
        Assertions.assertEquals(6,events.size)
    }

    @Test
    fun `2 days, 4 activities, 3 meals`() {
        val inputs = CalculatorInputs(3,0,100,TravelMode.WALKING,
            listOf(),9,30,60,0,60)
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))

        val days = listOf(day1,day2)
        val events = CalendarAdapter().getListOfEvents(days,inputs)

        //Debería haber 10 eventos porque tenemos 4 eventos turisticos, y 3 comidas por DIA
        Assertions.assertEquals(10,events.size)
    }

    @Test
    fun `3 days, 6 activities, 3 meals`() {
        val inputs = CalculatorInputs(3,0,100,TravelMode.WALKING,
            listOf(),9,30,60,0,60)
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))
        val day3 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))

        val days = listOf(day1,day2,day3)
        val events = CalendarAdapter().getListOfEvents(days,inputs)

        //Debería haber 10 eventos porque tenemos 4 eventos turisticos, y 3 comidas por DIA
        Assertions.assertEquals(15,events.size)
    }

    @Test
    fun `3 days, 6 activities, 3 meals with 2 free Days`() {
        val inputs = CalculatorInputs(3,2,100,TravelMode.WALKING,
            listOf(),9,30,60,0,60)
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))
        val day3 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))

        val days = listOf(day1,day2,day3)
        val events = CalendarAdapter().getListOfEvents(days,inputs)

        //Debería haber 10 eventos porque tenemos 4 eventos turisticos, y 3 comidas por DIA
        Assertions.assertEquals(17,events.size)
    }


    @Test
    fun `valid ics`(){
        val file = File("src/test/resources/calendar.ics")
        val content : InputStream = file.inputStream()

        val builder = CalendarBuilder()
        val calendar = builder.build(content)

        Assertions.assertEquals(17,calendar.components.size)
    }

    @Test
    fun `valid ics generated`(){
        val calendar = CalendarAdapter()
        val inputs = CalculatorInputs(3,2,100,TravelMode.WALKING,
            listOf(),9,30,60,0,60)
        val day1 = Day(1, mutableListOf(hotel, colloseum, fontanaDiTrevi))
        val day2 = Day(2, mutableListOf(hotel, colloseum, vaticano))

        val days = listOf(day1,day2)
        val events = calendar.getListOfEvents(days,inputs)

        val calendarIcs = calendar.createCalendar(events,
        DateDto(2022,7,29,9,0)
        )

        Assertions.assertEquals(12,calendarIcs.components.size)
        Assertions.assertEquals("Desayuno",calendarIcs.components[0].properties[3].value)
        Assertions.assertEquals("Día Libre",calendarIcs.components.last().properties[3].value)

    }




}

