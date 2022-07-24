package utn.triponometry.domain

data class Coordinates(val latitude: Double, val longitude: Double)

data class Place(val id: Int, val name: String, val distances: Map<Int, Int>)