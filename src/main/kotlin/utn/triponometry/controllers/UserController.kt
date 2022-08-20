package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.services.UserService

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {
    @PostMapping
    @ApiOperation(value = "Creates a new user (Sign Up / Register)")
    fun createUser(@RequestBody newUser: UserDto): ResponseEntity<Any> {
        val user = userService.createUser(newUser)
        return ResponseEntity.ok("User ${user.mail} created")
    }
}