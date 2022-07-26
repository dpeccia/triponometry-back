package utn.triponometry.helpers

import kotlin.random.Random
import kotlin.random.nextInt

object Random {
    fun pickFrom(range: IntRange) = Random.nextInt(range)
}