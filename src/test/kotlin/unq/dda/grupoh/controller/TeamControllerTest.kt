package unq.dda.grupoh.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import unq.dda.grupoh.controller.TeamController
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.service.TeamService

class TeamControllerTest {

    private val teamService = mock(TeamService::class.java)
    private val controller = TeamController(teamService)

    @Test
    fun getPlayersByTeamShouldReturnPlayerNames() {
        val team = "Boca"
        val players = listOf("Riquelme", "Palermo")
        `when`(teamService.getPlayersByTeamName(team)).thenReturn(players)

        val result = controller.getPlayersByTeam(team)

        assertEquals(players, result)
        verify(teamService).getPlayersByTeamName(team)
    }

    @Test
    fun getMatchesByTeamShouldReturnMatches() {
        val team = "River"
        val match = Match(
            id = 1,
            date = "2025-06-01",
            status = "FINISHED",
            stage = "Quarter Final",
            homeTeam = "River",
            awayTeam = "Boca",
            scoreHome = 2,
            scoreAway = 1,
            winner = "River"
        )
        `when`(teamService.getMatchesByTeamName(team)).thenReturn(listOf(match))

        val result = controller.getMatchesByTeam(team)

        assertEquals(listOf(match), result)
        verify(teamService).getMatchesByTeamName(team)
    }

    @Test
    fun getNextMatchesByTeamShouldReturnUpcomingMatches() {
        val team = "Independiente"
        val match = Match(
            id = 2,
            date = "2025-06-15",
            status = "SCHEDULED",
            stage = "Semi Final",
            homeTeam = "Independiente",
            awayTeam = "Racing",
            scoreHome = null,
            scoreAway = null,
            winner = null
        )
        `when`(teamService.getNextMatchesByTeamName(team)).thenReturn(listOf(match))

        val result = controller.getNextMatchesByTeam(team)

        assertEquals(listOf(match), result)
        verify(teamService).getNextMatchesByTeamName(team)
    }
}