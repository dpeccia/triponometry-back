package utn.triponometry.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import utn.triponometry.helpers.GoogleDistanceMatrixApiException
import utn.triponometry.helpers.GoogleGeocodeApiException
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.helpers.OpenWeatherException

@ControllerAdvice
class TriponometryExceptionHandler {
    @ExceptionHandler(
        OpenWeatherException::class,
        GoogleGeocodeApiException::class,
        GoogleDistanceMatrixApiException::class)
    fun mapExceptionToInternalServerErrorResponse(exception: RuntimeException) =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.message)

    @ExceptionHandler(
        IllegalUserException::class
    )
    fun mapExceptionToBadRequestResponse(exception: RuntimeException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.message)
}