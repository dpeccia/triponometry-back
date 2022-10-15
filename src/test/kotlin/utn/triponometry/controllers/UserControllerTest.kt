package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import utn.triponometry.domain.dtos.UserDto
import utn.triponometry.domain.dtos.UserDtoWithoutSensitiveInformation
import utn.triponometry.domain.dtos.UserRequest
import utn.triponometry.helpers.BadLoginException
import utn.triponometry.helpers.IllegalUserException
import utn.triponometry.helpers.TokenException
import utn.triponometry.services.UserService

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockkBean
    lateinit var userService: UserService

    @SpykBean
    lateinit var userController: UserController

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val userRequest = UserRequest("test@gmail.com", "1234","test")

    @Test
    fun `sign-up endpoint returns ok`() {
        every { userService.createUser(any(),any()) } returns UserDtoWithoutSensitiveInformation("1", userRequest.mail, userRequest.username,false,false)

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        ).andExpect(MockMvcResultMatchers.status().`is`(200)).andReturn().response.contentAsString

        val user = objectMapper.readValue<UserDtoWithoutSensitiveInformation>(responseAsString)

        assertEquals("1", user.id)
        assertEquals("test@gmail.com", user.mail)
    }

    @Test
    fun `sign-up endpoint returns an error`() {
        every { userService.createUser(any(),any()) } throws IllegalUserException("There is already an user under that email")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        ).andExpect(MockMvcResultMatchers.status().`is`(400)).andReturn().response.contentAsString

        val error = objectMapper.readValue<Map<String, String>>(responseAsString)

        assertEquals("There is already an user under that email", error["error"])
    }

    @Test
    fun `login endpoint returns ok`() {
        val user = UserDtoWithoutSensitiveInformation("1", userRequest.mail,userRequest.username,false,false)
        every { userService.checkUserCredentials(any()) } returns user
        every { userService.login(user) } returns ResponseCookie.from("X-Auth", "").build()

        mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        ).andExpect(MockMvcResultMatchers.status().`is`(200))
    }

    @Test
    fun `login endpoint returns an error`() {
        every { userService.checkUserCredentials(any()) } throws BadLoginException("Wrong username or password")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        ).andExpect(MockMvcResultMatchers.status().`is`(401)).andReturn().response.contentAsString

        val error = objectMapper.readValue<Map<String, String>>(responseAsString)

        assertEquals("Wrong username or password", error["error"])
    }

    @Test
    fun `logout endpoint returns ok`() {
        every { userController.checkAndGetUserId(any()) } returns ObjectId()
        every { userService.logout(any()) } returns ResponseCookie.from("X-Auth", "").build()

        mvc.perform(
            MockMvcRequestBuilders
                .delete("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(200))
    }

    @Test
    fun `logout endpoint returns an error`() {
        every { userController.checkAndGetUserId(any()) } throws TokenException("Token not found or expired. Login again")

        val responseAsString = mvc.perform(
            MockMvcRequestBuilders
                .delete("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().`is`(401)).andReturn().response.contentAsString

        val error = objectMapper.readValue<Map<String, String>>(responseAsString)

        assertEquals("Token not found or expired. Login again", error["error"])
    }
}