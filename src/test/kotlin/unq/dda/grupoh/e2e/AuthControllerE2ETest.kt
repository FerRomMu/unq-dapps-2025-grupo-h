package unq.dda.grupoh.e2e

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.junit.jupiter.api.Test

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerE2ETest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `login success returns token`() {
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
    fun `login failure returns unauthorized`() {
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
    fun `register success returns created`() {
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
    fun `register conflict returns conflict`() {
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