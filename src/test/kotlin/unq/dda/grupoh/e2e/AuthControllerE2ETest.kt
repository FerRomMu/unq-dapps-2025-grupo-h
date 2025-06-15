package unq.dda.grupoh.e2e

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerE2ETest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun loginSuccessReturnsToken() {
        val loginJson = """
            {
                "username": "admin",
                "password": "admin"
            }
        """.trimIndent()

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun loginFailureReturnsUnauthorized() {
        val loginJson = """
            {
                "username": "wrong",
                "password": "wrong"
            }
        """.trimIndent()

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson))
            .andExpect(status().isUnauthorized)
            .andExpect(content().string("Invalid credentials"))
    }

    @Test
    fun registerSuccessReturnsCreated() {
        val registerJson = """
            {
                "username": "newuser",
                "password": "newpass"
            }
        """.trimIndent()

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
            .andExpect(status().isCreated)
            .andExpect(content().string("User registered successfully"))
    }

    @Test
    fun registerConflictReturnsConflict() {
        val registerJson = """
            {
                "username": "admin",
                "password": "admin"
            }
        """.trimIndent()

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
            .andExpect(status().isConflict)
            .andExpect(content().string("Username already taken"))
    }
}