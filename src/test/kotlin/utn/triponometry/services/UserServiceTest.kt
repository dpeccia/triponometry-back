package utn.triponometry.services

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.helpers.Sha512Hash
import utn.triponometry.properties.Hash
import utn.triponometry.properties.TriponometryProperties
import utn.triponometry.repos.UserRepository
import java.util.*

class UserServiceTest {
    val userRepository: UserRepository = mockk()
    val sha512 = Sha512Hash(TriponometryProperties(hash = Hash("231223423423")))
    val userService = UserService(userRepository, sha512)

    @Test
    fun `user is created successfully`() {
        every { userRepository.findByMail(any()) } returns Optional.empty()
        every { userRepository.save(any()) } answers { firstArg() }

        val userDto = UserDto("test@gmail.com", "1234")
        val newUser = userService.createUser(userDto)

        assertNotNull(newUser.id)
        assertEquals("test@gmail.com", newUser.mail)
        assertNotEquals("1234", newUser.password)
    }

    @Test
    fun `user is not created if it already exists`() {
        every { userRepository.findByMail(any()) } answers { Optional.of(User(firstArg(), "23134")) }
        val userDto = UserDto("test@gmail.com", "1234")

        val exception = assertThrows<IllegalUserException> { userService.createUser(userDto) }
        assertEquals("There is already an user under that email", exception.message)
    }
}