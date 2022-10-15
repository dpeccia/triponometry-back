package utn.triponometry.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
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
import utn.triponometry.helpers.JwtSigner
import utn.triponometry.helpers.Sha512Hash
import utn.triponometry.repos.UserRepository
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {
    @Autowired
    lateinit var mvc: MockMvc
    @Autowired
    lateinit var jwtSigner: JwtSigner
    @Autowired
    lateinit var sha512: Sha512Hash
    @MockkBean
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = User("example_admin", "example_admin","username",false,false)
        user.id = ObjectId()

        every { userRepository.findByMailAndPassword("wrong_username", sha512.getSHA512("example_password")) } returns Optional.empty<User>()
        every { userRepository.findByMailAndPassword("example_username", sha512.getSHA512("wrong_password")) } returns Optional.empty<User>()
        every { userRepository.findByMailAndPassword("example_username", sha512.getSHA512("example_password")) } returns Optional.of(user)
    }

    @Test
    fun `login with wrong username returns UNAUTHORIZED`() {
        val userDto = UserDto("wrong_username", "example_password","username",false,false)
        mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    fun `login with wrong password returns UNAUTHORIZED`() {
        val userDto = UserDto("example_username", "wrong_password","username",false,false)
        mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(401))
    }

    @Test
    fun `successful login returns OK`() {
        val userDto = UserDto("example_username", "example_password","username",false,false)
        mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(200))
    }

    @Test
    fun `successful login returns JWT cookie`() {
        val userDto = UserDto("example_username", "example_password","username",false,false)
        val response = mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(200)).andReturn().response

        val jwtToken = response.cookies[0]?.value
        val validation = jwtSigner.validateJwt(jwtToken)
        val id = validation.body.subject

        assertEquals(user.id.toString(), id)
    }

    @Test
    fun `successful logout erases JWT cookie`() {
        val userDto = UserDto("example_username", "example_password","username",false,false)
        val logInResponse = mvc.perform(
            MockMvcRequestBuilders
                .post("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(MockMvcResultMatchers.status().`is`(200)).andReturn().response

        val jwtToken = logInResponse.cookies[0].value
        val validation = jwtSigner.validateJwt(jwtToken)
        val id = validation.body.subject

        assertEquals(user.id.toString(), id)
        assertNotEquals(0, logInResponse.cookies[0].maxAge)

        val logOutResponse = mvc.perform(
            MockMvcRequestBuilders
                .delete("/user/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(logInResponse.cookies[0])
        ).andExpect(MockMvcResultMatchers.status().`is`(200)).andReturn().response

        assertEquals(0, logOutResponse.cookies[0].maxAge)
    }
}