package unq.dda.grupoh.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.transaction.annotation.Transactional
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.repository.TeamRepository
import unq.dda.grupoh.external.FootballDataService

class TeamServiceTest {

    private val teamRepository: TeamRepository = mock()
    private val footballDataService: FootballDataService = mock()
    private val teamService = TeamService(teamRepository, footballDataService)

    @Test
    fun `saveOrUpdateByName saves team if update returns 0`() {
        val team = Team(name = "TeamA", apiId = 1, players = emptyList())
        whenever(teamRepository.updateByName(team)).thenReturn(0)
        whenever(teamRepository.save(team)).thenReturn(team)

        val result = teamService.saveOrUpdateByName(team)

        assertEquals(team, result)
        verify(teamRepository).updateByName(team)
        verify(teamRepository).save(team)
        verify(teamRepository, never()).findByName(any())
    }

    @Test
    fun `saveOrUpdateByName returns updated team if update was successful`() {
        val team = Team(name = "TeamB", apiId = 2, players = emptyList())
        whenever(teamRepository.updateByName(team)).thenReturn(1)
        whenever(teamRepository.findByName(team.name)).thenReturn(team)

        val result = teamService.saveOrUpdateByName(team)

        assertEquals(team, result)
        verify(teamRepository).updateByName(team)
        verify(teamRepository, never()).save(any())
        verify(teamRepository).findByName(team.name)
    }

    @Test
    fun `getTeam returns team from repository if found`() {
        val team = Team(name = "TeamC", apiId = 3, players = emptyList())
        whenever(teamRepository.findByName(team.name)).thenReturn(team)

        val result = teamService.getTeam(team.name)

        assertEquals(team, result)
        verify(teamRepository).findByName(team.name)
        verifyNoInteractions(footballDataService)
    }

    @Test
    fun `getTeam fetches from footballDataService and saves teams if not found in repository`() {
        val teamName = "TeamD"
        val mainTeam = Team(name = teamName, apiId = 4, players = emptyList())
        val allTeams = listOf(mainTeam, Team(name = "Other", apiId = 5, players = emptyList()))
        whenever(teamRepository.findByName(teamName)).thenReturn(null)
        whenever(footballDataService.findTeamByName(teamName)).thenReturn(Pair(mainTeam, allTeams))
        whenever(teamRepository.save(org.mockito.kotlin.any<Team>())).thenAnswer { it.arguments[0] as Team }

        val result = teamService.getTeam(teamName)

        assertEquals(mainTeam, result)
        verify(teamRepository).findByName(teamName)
        verify(footballDataService).findTeamByName(teamName)
        allTeams.forEach {
            verify(teamRepository).save(it)
        }
    }

    @Test
    fun `getTeam throws ResourceNotFoundException when no mainTeam returned by footballDataService`() {
        val teamName = "TeamE"
        val allTeams = listOf<Team>()
        whenever(teamRepository.findByName(teamName)).thenReturn(null)
        whenever(footballDataService.findTeamByName(teamName)).thenReturn(Pair(null, allTeams))

        val exception = assertThrows(ResourceNotFoundException::class.java) {
            teamService.getTeam(teamName)
        }

        assertEquals("Team not found.", exception.message)
        verify(teamRepository).findByName(teamName)
        verify(footballDataService).findTeamByName(teamName)
    }

    @Test
    fun `getPlayersByTeamName returns players if team has them`() {
        val teamName = "TeamF"
        val players = listOf("player1", "player2")
        val team = Team(name = teamName, apiId = 6, players = players)
        whenever(teamRepository.findByName(teamName)).thenReturn(team)

        val result = teamService.getPlayersByTeamName(teamName)

        assertEquals(players, result)
        verify(teamRepository).findByName(teamName)
        verifyNoMoreInteractions(footballDataService)
    }

    @Test
    fun `getPlayersByTeamName fetches team from footballDataService and updates if players empty`() {
        val teamName = "TeamG"
        val emptyTeam = Team(name = teamName, apiId = 7, players = emptyList())
        val fetchedTeam = Team(name = teamName, apiId = 7, players = listOf("playerA", "playerB"))

        whenever(teamRepository.findByName(teamName)).thenReturn(emptyTeam)
        whenever(footballDataService.findTeamById(7, teamName)).thenReturn(fetchedTeam)
        whenever(teamRepository.save(fetchedTeam)).thenReturn(fetchedTeam)

        val result = teamService.getPlayersByTeamName(teamName)

        assertEquals(fetchedTeam.players, result)
        verify(teamRepository).findByName(teamName)
        verify(footballDataService).findTeamById(7, teamName)
        verify(teamRepository).save(fetchedTeam)
    }

    @Test
    fun `getMatchesByTeamName returns matches from footballDataService`() {
        val teamName = "TeamH"
        val team = Team(name = teamName, apiId = 8, players = emptyList())
        val matches = listOf(
            mock<Match>(),
            mock()
        )

        whenever(teamRepository.findByName(teamName)).thenReturn(team)
        whenever(footballDataService.findMatchesByTeam(team)).thenReturn(matches)

        val result = teamService.getMatchesByTeamName(teamName)

        assertEquals(matches, result)
        verify(teamRepository).findByName(teamName)
        verify(footballDataService).findMatchesByTeam(team)
    }

    @Test
    fun `getNextMatchesByTeamName filters matches not played yet`() {
        val teamName = "TeamI"
        val team = Team(name = teamName, apiId = 9, players = emptyList())
        val matchPlayed: Match = mock {
            on { isNotPlayedYet() } doReturn false
        }
        val matchNotPlayed: Match = mock {
            on { isNotPlayedYet() } doReturn true
        }
        val allMatches = listOf(matchPlayed, matchNotPlayed)

        whenever(teamRepository.findByName(teamName)).thenReturn(team)
        whenever(footballDataService.findMatchesByTeam(team)).thenReturn(allMatches)

        val result = teamService.getNextMatchesByTeamName(teamName)

        assertEquals(listOf(matchNotPlayed), result)
        verify(teamRepository).findByName(teamName)
        verify(footballDataService).findMatchesByTeam(team)
    }
}