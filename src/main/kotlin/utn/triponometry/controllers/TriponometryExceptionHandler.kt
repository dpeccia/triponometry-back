package utn.triponometry.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import utn.triponometry.helpers.BadLoginException
import utn.triponometry.helpers.GoogleDistanceMatrixApiException
import utn.triponometry.helpers.GoogleGeocodeApiException
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.helpers.OpenWeatherException
import utn.triponometry.helpers.TokenException
import utn.triponometry.helpers.TriponometryException

@ControllerAdvice
class TriponometryExceptionHandler {
    @ExceptionHandler(
        OpenWeatherException::class,
        GoogleGeocodeApiException::class,
        GoogleDistanceMatrixApiException::class
    )
    fun mapExceptionToInternalServerErrorResponse(exception: TriponometryException) =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.dto())

    @ExceptionHandler(
        IllegalUserException::class
    )
    fun mapExceptionToBadRequestResponse(exception: TriponometryException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.dto())

    @ExceptionHandler(
        BadLoginException::class,
        TokenException::class
    )
    fun mapExceptionToUnauthorizedResponse(exception: TriponometryException) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.dto())
}