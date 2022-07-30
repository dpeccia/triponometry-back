package utn.triponometry.domain


data class Day(val number: Int, val route: MutableList<Place>) {
    fun hasSpaceFor(activity: Place, timePerDay: Int): Boolean {
        if (route.size == 1) return true

        val routeIfThisActivityIsAdded = route.map { it }.toMutableList()
        routeIfThisActivityIsAdded.add(activity)

        val lastTime = activity.durations[route.first().id] ?: 0

        val totalTime = routeIfThisActivityIsAdded.zipWithNext { from, to ->
            (from.durations[to.id] ?: 0) + (to.timeSpent ?: 0)
        }.sum() + lastTime

        return totalTime <= timePerDay
    }

    fun addToRoute(activity: Place) {
        route.add(activity)
    }
}