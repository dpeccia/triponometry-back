package utn.triponometry.services

import org.springframework.stereotype.Service
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.helpers.Sha512Hash
import utn.triponometry.repos.UserRepository

@Service
class UserService(private val userRepository: UserRepository, private val sha512: Sha512Hash) {
    fun createUser(newUser: UserDto): User {
        if (userRepository.findByMail(newUser.mail).isPresent)
            throw IllegalUserException("There is already an user under that email")

        val user = User(newUser.mail, sha512.getSHA512(newUser.password))

        return userRepository.save(user)
    }
}