package utn.triponometry.domain.external

import com.google.gson.Gson
import net.fortuna.ical4j.model.Calendar
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utn.triponometry.domain.external.dtos.EventTrip


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CalendarTest {

    val calAdapter = CalendarAdapter()


    @Test
    fun `Calendario de varios eventos`() {
        val cal = calAdapter.createCalendar(
            listOf(
                EventTrip("Museo",25,6,2022,9,0,3),
                EventTrip("Almuerzo",25,6,2022,13,0,1),
                EventTrip("Paseo",25,6,2022,14,30,3),
                EventTrip("Vuelta a casa",25,6,2022,17,30,2),
                EventTrip("Dia libre",26,6,2022,9,30,10),
                EventTrip("Paseo del día",27,6,2022,9,0,10)
            )
        )
        //calAdapter.createIcsFile(cal,"calendar.ics")

    }
}
