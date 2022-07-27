package utn.triponometry.domain.genetic_algorithm

import io.mockk.every
import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utn.triponometry.domain.Place
import utn.triponometry.helpers.Random

class CrossoverTest {
    val hotel = Place(0, "Hotel", mapOf(1 to 30, 2 to 60, 3 to 35))
    val colloseum = Place(1, "Colloseum", mapOf(0 to 25, 2 to 20, 3 to 25))
    val fontanaDiTrevi = Place(2, "Fontana Di Trevi", mapOf(0 to 50, 1 to 20, 3 to 25))
    val vaticano = Place(3, "Vaticano", mapOf(0 to 40, 1 to 30, 2 to 30))

    val trip1 = Individual(listOf(hotel, colloseum, fontanaDiTrevi, vaticano))
    val trip2 = Individual(listOf(colloseum, fontanaDiTrevi, vaticano, hotel))
    val trip3 = Individual(listOf(vaticano, fontanaDiTrevi, colloseum, hotel))
    val trip4 = Individual(listOf(hotel, fontanaDiTrevi, vaticano, colloseum))

    val individuals = listOf(trip1, trip2, trip3, trip4)

    @BeforeEach
    fun setup() {
        mockkObject(Random)
        every { Random.pickFrom(any()) } returns 1 andThen 3
    }

    @Test
    fun `crossover from a list of individuals returns a new list of individuals`() {
        val children = doCrossover(individuals)
        assertEquals(individuals.size, children.size)
    }

    @Test
    fun `crossover between 2 individuals is done with order crossover method`() {
        val children = orderCrossover(trip1, trip2)
        val child1 = children.first()
        val child2 = children.last()

        assertEquals(listOf(hotel, fontanaDiTrevi, colloseum, vaticano), child1.places)
        assertEquals(listOf(colloseum, vaticano, fontanaDiTrevi, hotel), child2.places)
    }

    @Test
    fun `order crossover inherits the cities before and after the points selected`() {
        val state = CrossoverState(trip1.places.toTypedArray(), trip2.places.toTypedArray())

        inheritPlaces(1, 3, 4, state)

        assertEquals("Hotel", state.child1[0]?.name)
        assertEquals("Vaticano", state.child1[3]?.name)
        assertNull(state.child1[1])
        assertNull(state.child1[2])

        assertEquals("Colloseum", state.child2[0]?.name)
        assertEquals("Hotel", state.child2[3]?.name)
        assertNull(state.child2[1])
        assertNull(state.child2[2])
    }

    @Test
    fun `order crossover gets the cities of the opposite parent if the child does not already contain them`() {
        val state = CrossoverState(trip1.places.toTypedArray(), trip2.places.toTypedArray())
        inheritPlaces(1, 3, 4, state)

        getPlacesFromOppositeParent(1, 3, state)

        assertEquals("Fontana Di Trevi", state.child1[1]?.name)
        assertNull(state.child1[2])

        assertEquals("Fontana Di Trevi", state.child2[2]?.name)
        assertNull(state.child2[1])
    }

    @Test
    fun `order crossover finds all the cities that are still missing from each child`() {
        val state = CrossoverState(trip1.places.toTypedArray(), trip2.places.toTypedArray())
        inheritPlaces(1, 3, 4, state)
        getPlacesFromOppositeParent(1, 3, state)

        findMissingChildrenPlaces(4, state)

        assertEquals(1, state.citiesNotInChild1.size)
        assertEquals("Colloseum", state.citiesNotInChild1[0].name)

        assertEquals(1, state.citiesNotInChild2.size)
        assertEquals("Vaticano", state.citiesNotInChild2[0].name)
    }

    @Test
    fun `order crossover finds which spots are still empty in each child`() {
        val state = CrossoverState(trip1.places.toTypedArray(), trip2.places.toTypedArray())
        inheritPlaces(1, 3, 4, state)
        getPlacesFromOppositeParent(1, 3, state)
        findMissingChildrenPlaces(4, state)

        findEmptyChildrenSpots(4, state)

        assertEquals(1, state.emptySpotsChild1.size)
        assertEquals(1, state.emptySpotsChild2.size)

        assertEquals(2, state.emptySpotsChild1[0])
        assertEquals(1, state.emptySpotsChild2[0])
    }

    @Test
    fun `order crossover fills in the empty spots`() {
        val state = CrossoverState(trip1.places.toTypedArray(), trip2.places.toTypedArray())
        inheritPlaces(1, 3, 4, state)
        getPlacesFromOppositeParent(1, 3, state)
        findMissingChildrenPlaces(4, state)
        findEmptyChildrenSpots(4, state)

        fillEmptySpots(state)

        assertEquals(0, state.emptySpotsChild1.size)
        assertEquals(0, state.emptySpotsChild2.size)
        assertTrue(state.child1.toList().all { it != null })
        assertTrue(state.child2.toList().all { it != null })
    }
}