package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import utn.triponometry.domain.User
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.services.UserService

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockkBean
    lateinit var userService: UserService

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val userDto = UserDto("test@gmail.com", "1234")

    @Test
    fun `sign-up endpoint returns ok`() {
        every { userService.createUser(any()) } returns User(userDto.mail, "2423423")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(200)).andReturn().response.contentAsString

        assertEquals("User test@gmail.com created", responseAsString)
    }

    @Test
    fun `sign-up endpoint returns an error`() {
        every { userService.createUser(any()) } throws IllegalUserException("There is already an user under that email")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(400)).andReturn().response.contentAsString

        assertEquals("There is already an user under that email", responseAsString)
    }
}