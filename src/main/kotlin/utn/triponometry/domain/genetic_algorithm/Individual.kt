package utn.triponometry.domain.genetic_algorithm

import utn.triponometry.domain.Place

data class Individual(val places: List<Place>) {
    private val firstIsNotHotelPenalization = if (places.first().id != 0) 0.1 else 1.0

    private val lastTime: Int = places.last().durations[places.first().id] ?: 0

    val totalTime = places.zipWithNext { from, to -> from.durations[to.id] ?: 0 }.sum() + lastTime

    val fitness = (1.toDouble() / totalTime) * firstIsNotHotelPenalization
}