package utn.triponometry.services

import jdk.jshell.spi.ExecutionControl
import org.bson.types.ObjectId
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.WebUtils
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation
import utn.triponometry.domain.dtos.UserLogin
import utn.triponometry.domain.dtos.UserRequest
import utn.triponometry.domain.external.dtos.TripDto
import utn.triponometry.helpers.*
import utn.triponometry.repos.UserRepository
import javax.servlet.http.HttpServletRequest

@Service
class UserService(private val userRepository: UserRepository, private val sha512: Sha512Hash) {
    fun createUser(newUser: UserRequest): UserDtoWithoutSensitiveInformation {
        if (userRepository.findByMail(newUser.mail).isPresent)
            throw IllegalUserException("Ya existe un usuario registrado con ese mail")
        if (userRepository.findByUsername(newUser.username).isPresent)
            throw IllegalUserException("Ya existe un usuario llamado ${newUser.username}. Por favor, elegí uno nuevo")

        val user = User(newUser.mail, sha512.getSHA512(newUser.password), newUser.username, false)

        return userRepository.save(user).dto()
    }

    fun checkUserLogin(user: ObjectId): UserDtoWithoutSensitiveInformation =
        userRepository.findById(user).get().dto()

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

    fun checkUserIsPresent(newUser: UserRequest): Boolean =
        userRepository.findByMail(newUser.mail).isPresent

    fun checkUserCredentials(userDto: UserLogin) =
        userRepository.findByMailAndPassword(userDto.mail, sha512.getSHA512(userDto.password))
            .orElseThrow { BadLoginException("Usuario y/o contraseña inválida") }.dto()

    fun verifyUser(username: String): UserDtoWithoutSensitiveInformation {
        val userOptional = userRepository.findByUsername(username)
        if (userOptional.isPresent){
            val user = userOptional.get()
            user.verified = true
            return userRepository.save(user).dto()
        }
        throw IllegalUserException("No existe el usuario: $username")
    }

    fun getUser(id: ObjectId): UserDtoWithoutSensitiveInformation =
        userRepository.findById(id).orElseThrow { IllegalUserException("El usuario no existe") }.dto()

}

