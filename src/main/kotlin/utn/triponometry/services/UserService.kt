package utn.triponometry.services

import org.bson.types.ObjectId
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import org.springframework.web.util.WebUtils
import utn.triponometry.domain.Code
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.*
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation
import utn.triponometry.domain.dtos.UserLogin
import utn.triponometry.domain.dtos.UserRequest
import utn.triponometry.helpers.*
import utn.triponometry.repos.CodeRepository
import utn.triponometry.repos.UserRepository
import javax.servlet.http.HttpServletRequest

@Service
class UserService(
    private val userRepository: UserRepository,
    private val sha512: Sha512Hash,
    private val codeRepository: CodeRepository
) {
    fun createUser(newUser: UserRequest, googleAccount: Boolean): UserDtoWithoutSensitiveInformation {
        if (userRepository.findByMail(newUser.mail).isPresent)
            throw IllegalUserException("Ya existe un usuario registrado con ese mail")
        if (userRepository.findByUsername(newUser.username).isPresent)
            throw IllegalUserException("Ya existe un usuario llamado ${newUser.username}. Por favor, elegí uno nuevo")

        val user = User(newUser.mail, sha512.getSHA512(newUser.password), newUser.username, false, googleAccount)

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

    fun updatePassword(id: ObjectId, request: PasswordRequest): UserDtoWithoutSensitiveInformation {
        val user = userRepository.findById(id).get()

        val oldPassword = sha512.getSHA512(request.oldPassword)
        if (user.password != oldPassword)
            throw IllegalUserException("La contraseña anterior no es correcta")

        user.password = sha512.getSHA512(request.newPassword)
        return userRepository.save(user).dto()
    }

    fun sendEmail(emailReq: String): Code {
        val emailSender = EmailSender()
        val user = userRepository.findByMail(emailReq)
            .orElseThrow { IllegalUserException("El email ingresado es incorrecto") }

        if (user.googleAccount)
            throw IllegalUserException("No se puede actualizar la contraseña de una cuenta de Google")
        val passwordCode = emailSender.generateCode()
        val code = Code(user, passwordCode)
        emailSender.sendEmail(emailReq, passwordCode)
        return codeRepository.save(code)

    }

    fun verifyPasswordCode(userEmail: String, code: String): CodeRequest {
        val user = userRepository.findByMail(userEmail)
            .orElseThrow { IllegalUserException("El email ingresado es incorrecto") }
        return codeRepository.findByUserIdAndCode(user.id!!, code)
            .orElseThrow { IllegalUserException("El código ingresado es incorrecto o expiró") }.code()

    }

    fun recoverPassword(userEmail: String, code: String, newPassword: String): UserDtoWithoutSensitiveInformation {

        val user = userRepository.findByMail(userEmail)
            .orElseThrow { IllegalUserException("El email ingresado es incorrecto") }

        val code = codeRepository.findByUserIdAndCode(user.id!!, code)
            .orElseThrow { IllegalUserException("El código ingresado es incorrecto o expiró") }

        codeRepository.delete(code)

        user.password = sha512.getSHA512(newPassword)
        return userRepository.save(user).dto()
    }
}

