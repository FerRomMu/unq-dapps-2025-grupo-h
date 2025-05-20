package unq.dda.grupoh.e2e

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class TeamControllerE2ETest(@Autowired val mockMvc: MockMvc) {

    private var token: String = ""

    @BeforeEach
    fun authenticate() {
        val loginJson = """
            {
                "username": "admin",
                "password": "admin"
            }
        """.trimIndent()

        val mvcResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginJson))
            .andExpect(status().isOk)
            .andReturn()

        val responseJson = mvcResult.response.contentAsString
        val jsonNode = jacksonObjectMapper().readTree(responseJson)
        token = jsonNode.get("token").asText()
    }

    @Test
    fun `get players by team name returns list`() {
        mockMvc.perform(get("/team/players")
            .param("name", "Leverkusen")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0]").isNotEmpty)
    }

    @Test
    fun `get matches by team name returns list`() {
        mockMvc.perform(get("/team/matches")
            .param("name", "Leverkusen")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].id").isNumber)
            .andExpect(jsonPath("$[0].date").isNotEmpty)
            .andExpect(jsonPath("$[0].homeTeam").value(
                Matchers.anyOf(
                    Matchers.containsString("Leverkusen"),
                    Matchers.notNullValue()
                )
            ))
    }
}