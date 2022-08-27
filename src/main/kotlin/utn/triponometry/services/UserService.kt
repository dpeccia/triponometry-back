package utn.triponometry.services

import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.WebUtils
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation
import utn.triponometry.helpers.BadLoginException
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.helpers.JwtSigner
import utn.triponometry.helpers.Sha512Hash
import utn.triponometry.repos.UserRepository
import javax.servlet.http.HttpServletRequest

@Service
class UserService(private val userRepository: UserRepository, private val sha512: Sha512Hash) {
    fun createUser(newUser: UserDto): UserDtoWithoutSensitiveInformation {
        if (userRepository.findByMail(newUser.mail).isPresent)
            throw IllegalUserException("There is already an user under that email")

        val user = User(newUser.mail, sha512.getSHA512(newUser.password))

        return userRepository.save(user).dto()
    }

    fun login(user: UserDtoWithoutSensitiveInformation): ResponseCookie {
        val jwt = JwtSigner.createJwt(user.id)
        return ResponseCookie.fromClientResponse("X-Auth", jwt)
            .maxAge(3600)
            .httpOnly(true)
            .path("/")
            .secure(false)
            .build()
    }

    fun logout(request: HttpServletRequest) =
        ResponseCookie.fromClientResponse("X-Auth", WebUtils.getCookie(request, "X-Auth")!!.value)
            .maxAge(0)
            .httpOnly(true)
            .path("/")
            .secure(false)
            .build()

    fun checkUserCredentials(userDto: UserDto) =
        userRepository.findByMailAndPassword(userDto.mail, sha512.getSHA512(userDto.password))
            .orElseThrow { BadLoginException("Wrong username or password") }.dto()
}