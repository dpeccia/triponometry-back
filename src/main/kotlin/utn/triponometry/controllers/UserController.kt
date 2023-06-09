package utn.triponometry.controllers

import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import utn.triponometry.domain.dtos.*
import utn.triponometry.services.UserService
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user", produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController(private val userService: UserService): BaseController() {
    @PostMapping
    @ApiOperation(value = "Creates a new user (Sign Up / Register)")
    fun createUser(@RequestBody newUser: UserRequest): ResponseEntity<Any> {
        val user = userService.createUser(newUser,false)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/tokens")
    @ApiOperation(value = "Log In")
    fun login(@RequestBody userDto: UserLogin): ResponseEntity<Any> {
        val user = userService.checkUserCredentials(userDto)
        val authCookie = userService.login(user)
        return ResponseEntity.ok()
            .header("Set-Cookie", authCookie.toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Set-Cookie")
            .body(user)
    }
    @PostMapping("/gtokens")
    @ApiOperation(value = "Google Log In")
    fun googleLogIn(@RequestBody userDto: UserRequest): ResponseEntity<Any> {
        if(!userService.checkUserIsPresent(userDto)){
            userService.createUser(userDto, true)
        }
        return login(userDto.userLogin())
    }

    @DeleteMapping("/tokens")
    @ApiOperation(value = "Log Out")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        checkAndGetUserId(request)
        val authCookie = userService.logout(request)
        return ResponseEntity.ok().header("Set-Cookie", authCookie.toString()).build()
    }

    @GetMapping
    @ApiOperation(value = "Check Login")
    fun checkLogin(request: HttpServletRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val response = userService.checkUserLogin(userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify/{username}")
    @ApiOperation(value = "Verify user")
    fun verify(@PathVariable username: String): ResponseEntity<Any> {
        val response = userService.verifyUser(username)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get User Info")
    fun getUser(request: HttpServletRequest, @PathVariable id: String): ResponseEntity<Any> {
        checkAndGetUserId(request)
        val response = userService.getUser(ObjectId(id))
        return ResponseEntity.ok(response)
    }

    @PostMapping("/password")
    @ApiOperation(value = "Change user password")
    fun updatePassword(request: HttpServletRequest, @RequestBody body: PasswordRequest): ResponseEntity<Any> {
        val userId = checkAndGetUserId(request)
        val response = userService.updatePassword(userId, body)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/password/email")
    @ApiOperation(value = "Send email to recover password")
    fun recoverPasswordEmail(@RequestBody emailRequest: EmailRequest): ResponseEntity<Any> {
        val response = userService.sendEmail(emailRequest.email)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/password/verify")
    @ApiOperation(value = "Verify password code")
    fun verifyPasswordCode(@RequestBody codeRequest: CodeRequest): ResponseEntity<Any> {
        val response = userService.verifyPasswordCode(codeRequest.email, codeRequest.code)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/password/recover")
    @ApiOperation(value = "Recover password")
    fun resetPassword(@RequestBody request: PasswordRecoverRequest): ResponseEntity<Any> {
        val response = userService.recoverPassword(request.email, request.code, request.newPassword)
        return ResponseEntity.ok(response)
    }

}