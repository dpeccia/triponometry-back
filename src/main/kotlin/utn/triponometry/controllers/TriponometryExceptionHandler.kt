package utn.triponometry.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import utn.triponometry.helpers.OpenWeatherException

@ControllerAdvice
class TriponometryExceptionHandler {
    @ExceptionHandler(OpenWeatherException::class)
    fun mapExceptionToInternalServerErrorResponse(exception: OpenWeatherException) =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.message)
}