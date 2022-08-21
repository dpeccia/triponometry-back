package utn.triponometry.helpers

open class TriponometryException(message: String): RuntimeException(message) {
    fun dto() = mapOf("error" to message)
}

class OpenWeatherException(message: String) : TriponometryException(message)

class AmazonException(message: String) : TriponometryException(message)
class GoogleGeocodeApiException(message: String) : TriponometryException(message)
class GoogleDistanceMatrixApiException(message: String) : TriponometryException(message)

class IllegalUserException(message: String): TriponometryException(message)
class BadLoginException(message: String): TriponometryException(message)
class TokenException(message: String): TriponometryException(message)