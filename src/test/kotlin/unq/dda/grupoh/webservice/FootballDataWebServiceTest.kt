package unq.dda.grupoh.webservice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.slf4j.Logger
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import unq.dda.grupoh.model.Team
import unq.dda.grupoh.exceptions.ExternalErrorException
import unq.dda.grupoh.dto.footballData.ApiFiltersDTO
import unq.dda.grupoh.dto.footballData.TeamResponse
import unq.dda.grupoh.dto.footballData.AreaDTO
import unq.dda.grupoh.dto.footballData.CompetitionDTO
import unq.dda.grupoh.dto.footballData.FiltersDTO
import unq.dda.grupoh.dto.footballData.FootballTeamDTO
import unq.dda.grupoh.dto.footballData.MatchDTO
import unq.dda.grupoh.dto.footballData.MatchesResponse
import unq.dda.grupoh.dto.footballData.PlayerDTO
import unq.dda.grupoh.dto.footballData.ResultSetDTO
import unq.dda.grupoh.dto.footballData.ScoreDTO
import unq.dda.grupoh.dto.footballData.ScoreValueDTO
import unq.dda.grupoh.dto.footballData.SeasonDTO
import unq.dda.grupoh.dto.footballData.TeamDetailResponse
import unq.dda.grupoh.dto.footballData.TeamInfoDTO

class FootballDataWebServiceTest {

    private val objectMapper = jacksonObjectMapper()

    private val mockTeamResponseFound: String = objectMapper.writeValueAsString(
        TeamResponse(
            count = 1,
            filters = ApiFiltersDTO(limit = 500, offset = 0, permission = "FREE"),
            teams = listOf(
                FootballTeamDTO(
                    id = 101,
                    name = "Real Madrid",
                    shortName = "RMA",
                    tla = "RMD",
                    crest = null,
                    address = null,
                    website = null,
                    founded = 1902,
                    clubColors = "White / Gold",
                    venue = "Santiago Bernabéu",
                    lastUpdated = "2023-01-01T00:00:00Z"
                )
            )
        )
    )

    private val mockTeamResponseNotFound: String = objectMapper.writeValueAsString(
        TeamResponse(
            count = 0,
            filters = ApiFiltersDTO(limit = 500, offset = 0, permission = "FREE"),
            teams = emptyList()
        )
    )

    private val mockTeamDetailResponseWithPlayers: String = objectMapper.writeValueAsString(
        TeamDetailResponse(
            area = AreaDTO(id = 222, name = "Spain", code = "ESP", flag = null),
            id = 101,
            name = "Real Madrid",
            shortName = "RMA",
            tla = "RMD",
            crest = null,
            address = null,
            website = null,
            founded = 1902,
            clubColors = "White / Gold",
            venue = "Santiago Bernabéu",
            runningCompetitions = emptyList(),
            coach = null,
            squad = listOf(
                PlayerDTO(id = 1, name = "Player One", position = "Forward", dateOfBirth = null, nationality = null),
                PlayerDTO(id = 2, name = "Player Two", position = "Midfielder", dateOfBirth = null, nationality = null)
            ),
            staff = emptyList(),
            lastUpdated = "2023-01-01T00:00:00Z"
        )
    )

    private val mockMatchesResponse: String = objectMapper.writeValueAsString(
        MatchesResponse(
            filters = FiltersDTO(),
            resultSet = ResultSetDTO(count = 1, first = "", last = "", played = 1, wins = 1, draws = 0, losses = 0),
            matches = listOf(
                MatchDTO(
                    area = AreaDTO(id = 222, name = "Spain", code = "ESP", flag = null),
                    competition = CompetitionDTO(id = 2014, name = "La Liga", code = null, type = null, emblem = null),
                    season = SeasonDTO(id = 1, startDate = "2023-01-01", endDate = "2023-06-30"),
                    id = 12345,
                    utcDate = "2023-10-26T18:00:00Z",
                    status = "FINISHED",
                    matchday = 10,
                    stage = "REGULAR_SEASON",
                    group = null,
                    lastUpdated = "2023-10-26T20:00:00Z",
                    homeTeam = TeamInfoDTO(id = 101, name = "Real Madrid"),
                    awayTeam = TeamInfoDTO(id = 102, name = "Barcelona"),
                    score = ScoreDTO(
                        winner = "HOME_TEAM",
                        duration = "REGULAR",
                        fullTime = ScoreValueDTO(home = 3, away = 1),
                        halfTime = ScoreValueDTO(home = 1, away = 0)
                    ),
                    odds = null,
                    referees = emptyList()
                )
            )
        )
    )

    private val mockEmptyMatchesResponse: String = objectMapper.writeValueAsString(
        MatchesResponse(
            filters = FiltersDTO(),
            resultSet = ResultSetDTO(count = 0, first = "", last = "", played = 0, wins = 0, draws = 0, losses = 0),
            matches = emptyList()
        )
    )

    @Test
    fun `findTeamByName should return team with players when found`() {
        val httpClient: HttpClient = mock()
        val logger: Logger = mock()

        val httpResponseTeams: HttpResponse<String> = mock()
        whenever(httpResponseTeams.statusCode()).thenReturn(200)
        whenever(httpResponseTeams.body()).thenReturn(mockTeamResponseFound)

        val httpResponsePlayers: HttpResponse<String> = mock()
        whenever(httpResponsePlayers.statusCode()).thenReturn(200)
        whenever(httpResponsePlayers.body()).thenReturn(mockTeamDetailResponseWithPlayers)

        whenever(
            httpClient.send(
                argThat { request -> request != null && request.uri().toString().contains("/teams?limit=") },
                any<HttpResponse.BodyHandler<String>>()
            )
        ).thenReturn(httpResponseTeams)

        whenever(
            httpClient.send(
                argThat { request -> request != null && request.uri().toString().contains("/teams/101") },
                any<HttpResponse.BodyHandler<String>>()
            )
        ).thenReturn(httpResponsePlayers)

        val service = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )

        val teamName = "RMA"

        val (team, allTeams) = service.findTeamByName(teamName)

        assertNotNull(team)
        assertEquals("RMA", team.name)
        assertEquals(101, team.apiId)
        assertEquals(listOf("Player One", "Player Two"), team.players)
        assertTrue(allTeams.isNotEmpty())

        verify(httpClient, times(2)).send(any(), any<HttpResponse.BodyHandler<String>>())
        verify(logger).info(startsWith("Equipo -- Team(teamId=null, name=RMA, apiId=101, players=[]"))
    }

    @Test
    fun `findTeamByName should return null team when not found`() {
        val httpClient: HttpClient = mock()
        val httpResponse: HttpResponse<String> = mock()
        val logger: Logger = mock()

        val footballDataService = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )
        val teamName = "NonExistent Team"
        whenever(httpResponse.statusCode()).thenReturn(200)
        whenever(httpResponse.body()).thenReturn(mockTeamResponseNotFound) // Simula una respuesta vacía
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
            .thenReturn(httpResponse)

        val (team, allTeams) = footballDataService.findTeamByName(teamName)

        assertNull(team)
        assertTrue(allTeams.isEmpty())
        verify(httpClient, times(1)).send(any(), any<HttpResponse.BodyHandler<String>>())
        verify(logger).info(eq("Equipo -- null"))
    }

    @Test
    fun `findTeamById should return team with players`() {
        val httpClient: HttpClient = mock()
        val httpResponse: HttpResponse<String> = mock()
        val logger: Logger = mock()

        val footballDataService = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )
        val apiId = 101
        val teamName = "Real Madrid"
        whenever(httpResponse.statusCode()).thenReturn(200)
        whenever(httpResponse.body()).thenReturn(mockTeamDetailResponseWithPlayers)
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
            .thenReturn(httpResponse)

        val team = footballDataService.findTeamById(apiId, teamName)

        assertNotNull(team)
        assertEquals(teamName, team.name)
        assertEquals(apiId, team.apiId)
        assertEquals(listOf("Player One", "Player Two"), team.players)
        verify(httpClient).send(
            argThat { request: HttpRequest ->
                request.uri().toString().replace("//", "/").replace("https:/", "https://") ==
                        "https://api.football-data.org/v4/teams/$apiId" },
            any<HttpResponse.BodyHandler<String>>()
        )
    }

    @Test
    fun `findTeamById should throw ExternalErrorException if fetching players fails`() {
        val httpClient: HttpClient = mock()
        val httpResponse: HttpResponse<String> = mock()
        val logger: Logger = mock()

        val footballDataService = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )
        val apiId = 101
        val teamName = "Real Madrid"
        whenever(httpResponse.statusCode()).thenReturn(404)
        whenever(httpResponse.body()).thenReturn("Team Not Found")
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
            .thenReturn(httpResponse)

        val exception = assertThrows<ExternalErrorException> {
            footballDataService.findTeamById(apiId, teamName)
        }
        assertTrue(exception.message!!.contains("Error fetching players for team $apiId: 404 - Team Not Found"))
        verify(httpClient).send(any(), any<HttpResponse.BodyHandler<String>>())
    }

    @Test
    fun `findMatchesByTeam should return list of matches`() {
        // Given: Configuración específica para este test
        val httpClient: HttpClient = mock()
        val httpResponse: HttpResponse<String> = mock()
        val logger: Logger = mock()

        val footballDataService = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )
        val team = Team(name = "Real Madrid", apiId = 101)
        whenever(httpResponse.statusCode()).thenReturn(200)
        whenever(httpResponse.body()).thenReturn(mockMatchesResponse)
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
            .thenReturn(httpResponse)

        // When
        val matches = footballDataService.findMatchesByTeam(team)

        // Then
        assertEquals(1, matches.size)
        with(matches[0]) {
            assertEquals(12345, id)
            assertEquals("2023-10-26T18:00:00Z", date)
            assertEquals("FINISHED", status)
            assertEquals("REGULAR_SEASON", stage)
            assertEquals("Real Madrid", homeTeam)
            assertEquals("Barcelona", awayTeam)
            assertEquals(3, scoreHome)
            assertEquals(1, scoreAway)
            assertEquals("HOME_TEAM", winner)
        }
        verify(httpClient).send(
            argThat { request: HttpRequest ->
                request.uri().toString()
                    .replace("//", "/")
                    .replace("https:/", "https://") ==
                        "https://api.football-data.org/v4/teams/${team.apiId}/matches" },
            any<HttpResponse.BodyHandler<String>>()
        )
    }

    @Test
    fun `findMatchesByTeam should return empty list if no matches found`() {
        val httpClient: HttpClient = mock()
        val httpResponse: HttpResponse<String> = mock()
        val logger: Logger = mock()

        val footballDataService = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )
        val team = Team(name = "Real Madrid", apiId = 101)
        whenever(httpResponse.statusCode()).thenReturn(200)
        whenever(httpResponse.body()).thenReturn(mockEmptyMatchesResponse)
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
            .thenReturn(httpResponse)

        val matches = footballDataService.findMatchesByTeam(team)

        assertTrue(matches.isEmpty())
        verify(httpClient).send(any(), any<HttpResponse.BodyHandler<String>>())
    }

    @Test
    fun `findMatchesByTeam should throw ExternalErrorException if fetching matches fails`() {
        val httpClient: HttpClient = mock()
        val httpResponse: HttpResponse<String> = mock()
        val logger: Logger = mock()

        val footballDataService = FootballDataWebService(
            apiToken = "test-token",
            verboseLogging = "false",
            client = httpClient,
            logger = logger
        )
        val team = Team(name = "Real Madrid", apiId = 101)
        whenever(httpResponse.statusCode()).thenReturn(500)
        whenever(httpResponse.body()).thenReturn("Server Error")
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
            .thenReturn(httpResponse)

        val exception = assertThrows<ExternalErrorException> {
            footballDataService.findMatchesByTeam(team)
        }
        assertTrue(exception.message!!.contains("Error fetching matches for team ${team.apiId}: 500 - Server Error"))
        verify(httpClient).send(any(), any<HttpResponse.BodyHandler<String>>())
    }
}
