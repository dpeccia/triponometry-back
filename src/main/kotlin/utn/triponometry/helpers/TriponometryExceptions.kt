package utn.triponometry.helpers

class OpenWeatherException(message: String) : RuntimeException(message)
class GoogleGeocodeApiException(message: String) : RuntimeException(message)
class GoogleDistanceMatrixApiException(message: String) : RuntimeException(message)