package unq.dda.grupoh.e2e

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import unq.dda.grupoh.webservice.FootballDataWebService
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.model.Team

@SpringBootTest
@AutoConfigureMockMvc
class TeamControllerE2ETest(@Autowired val mockMvc: MockMvc) {

    @MockitoBean
    lateinit var footballDataService: FootballDataWebService

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

        // --- MOCKEO ---
        val mockTeam = Team(
            teamId = null,
            name = "Leverkusen",
            apiId = 123,
            players = listOf("Jugador 1", "Jugador 2")
        )

        val mockMatch = Match(
            id = 999,
            date = "2025-01-01",
            status = "FINISHED",
            stage = "GROUP_STAGE",
            homeTeam = "Leverkusen",
            awayTeam = "Otro Team",
            scoreHome = 2,
            scoreAway = 1,
            winner = "HOME_TEAM"
        )

        whenever(footballDataService.findTeamByName("Leverkusen"))
            .thenReturn(mockTeam to listOf(mockTeam))

        whenever(footballDataService.findMatchesByTeam(any()))
            .thenReturn(listOf(mockMatch))
    }

    @Test
    fun getPlayersByTeamNameReturnsList() {
        mockMvc.perform(get("/team/players")
            .param("name", "Leverkusen")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0]").value("Jugador 1"))
    }

    @Test
    fun getMatchesByTeamNameReturnsList() {
        mockMvc.perform(get("/team/matches")
            .param("name", "Leverkusen")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].id").value(999))
            .andExpect(jsonPath("$[0].date").value("2025-01-01"))
            .andExpect(jsonPath("$[0].homeTeam").value("Leverkusen"))
    }
}