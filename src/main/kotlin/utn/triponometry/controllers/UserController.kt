package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.services.UserService
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user", produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(private val userService: UserService): BaseController() {
    @PostMapping
    @ApiOperation(value = "Creates a new user (Sign Up / Register)")
    fun createUser(@RequestBody newUser: UserDto): ResponseEntity<Any> {
        val user = userService.createUser(newUser)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/tokens")
    @ApiOperation(value = "Log In")
    fun login(@RequestBody userDto: UserDto): ResponseEntity<Any> {
        val user = userService.checkUserCredentials(userDto)
        val authCookie = userService.login(user)
        return ResponseEntity.ok()
            .header("Set-Cookie", authCookie.toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Set-Cookie")
            .body(user)
    }

    @DeleteMapping("/tokens")
    @ApiOperation(value = "Log Out")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        checkAndGetUserId(request)
        val authCookie = userService.logout(request)
        return ResponseEntity.ok().header("Set-Cookie", authCookie.toString()).build()
    }
}