package unq.dda.grupoh.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Feature
import unq.dda.grupoh.webservice.FootballDataWebService
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.model.TeamFeatures
import unq.dda.grupoh.repository.TeamFeaturesRepository
import unq.dda.grupoh.repository.TeamRepository
import unq.dda.grupoh.webservice.WhoScoreWebService

class TeamServiceTest {

    private val teamRepository: TeamRepository = mock()
    private val footballDataService: FootballDataWebService = mock()
    private val whoScoreWebService: WhoScoreWebService = mock()
    private val teamFeaturesRepository: TeamFeaturesRepository = mock()
    private val teamService = TeamService(teamRepository, teamFeaturesRepository, footballDataService, whoScoreWebService)
    val teamAstr = "TeamA"
    val teamBstr = "TeamB"

    @Test
    fun getTeamReturnsTeamFromRepositoryIfFound() {
        val team = Team(name = "TeamC", apiId = 3, players = emptyList())
        whenever(teamRepository.findByName(team.name)).thenReturn(team)

        val result = teamService.getTeam(team.name)

        assertEquals(team, result)
        verify(teamRepository).findByName(team.name)
        verifyNoInteractions(footballDataService)
    }

    @Test
    fun getTeamFetchesFromFootballDataServiceAndSavesTeamsIfNotFoundInRepository() {
        val teamName = "TeamD"
        val mainTeam = Team(name = teamName, apiId = 4, players = emptyList())
        val allTeams = listOf(mainTeam, Team(name = "Other", apiId = 5, players = emptyList()))
        whenever(teamRepository.findByName(teamName)).thenReturn(null)
        whenever(footballDataService.findTeamByName(teamName)).thenReturn(Pair(mainTeam, allTeams))
        whenever(teamRepository.save(org.mockito.kotlin.any<Team>())).thenAnswer { it.arguments[0] as Team }

        val result = teamService.getTeam(teamName)

        assertEquals(mainTeam, result)
        verify(footballDataService).findTeamByName(teamName)
        allTeams.forEach {
            verify(teamRepository).save(it)
        }
    }

    @Test
    fun getTeamThrowsResourceNotFoundExceptionWhenNoMainTeamReturnedByFootballDataService() {
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
    fun getTeamSavesAllTeamsEvenIfMainTeamIsNull() {
        val teamName = "TeamF"
        val team1 = Team(name = "Sub1", apiId = 6, players = emptyList())
        val team2 = Team(name = "Sub2", apiId = 7, players = emptyList())
        val allTeams = listOf(team1, team2)

        whenever(teamRepository.findByName(teamName)).thenReturn(null)
        whenever(footballDataService.findTeamByName(teamName)).thenReturn(Pair(null, allTeams))
        whenever(teamRepository.save(org.mockito.kotlin.any<Team>())).thenAnswer { it.arguments[0] as Team }

        assertThrows(ResourceNotFoundException::class.java) {
            teamService.getTeam(teamName)
        }
        verify(teamRepository).save(team1)
        verify(teamRepository).save(team2)
    }

    @Test
    fun getPlayersByTeamNameReturnsPlayersIfTeamHasThem() {
        val teamName = "TeamF"
        val players = listOf("player1", "player2")
        val team = Team(name = teamName, apiId = 6, players = players)
        whenever(teamRepository.findByName(teamName)).thenReturn(team)

        val result = teamService.getPlayersByTeamName(teamName)

        assertEquals(players, result)
        verifyNoMoreInteractions(footballDataService)
    }

    @Test
    fun getPlayersByTeamNameFetchesTeamFromFootballDataServiceAndUpdatesIfPlayersEmpty() {
        val teamName = "TeamG"
        val emptyTeam = Team(teamId = 1, name = teamName, apiId = 7, players = emptyList())
        val fetchedTeam = Team(name = teamName, apiId = 7, players = listOf("playerA", "playerB"))
        val wantedTeam = Team(teamId = 1, name = teamName, apiId = 7, players = listOf("playerA", "playerB"))

        whenever(teamRepository.findByName(teamName)).thenReturn(emptyTeam)
        whenever(teamRepository.save(wantedTeam)).thenReturn(wantedTeam)
        whenever(whoScoreWebService.findPlayersByTeamName(teamName)).thenReturn(
            listOf(
                Player(
                    name = "playerA",
                    teamName = "teamA",
                    age = 25,
                    position = "Forward",
                    heightCm = 180,
                    weightKg = 75,
                    matchesPlayed = 10,
                    minutesPlayed = 800,
                    goals = 5,
                    assists = 2,
                    yellowCards = 1,
                    redCards = 0,
                    shotsPerGame = 3.5,
                    passSuccessPercentage = 82.0,
                    aerialsWonPerGame = 1.2,
                    manOfTheMatch = 1,
                    rating = 7.8,
                    id = 1
                ),
                Player(
                    name = "playerB",
                    teamName = "teamB",
                    age = 27,
                    position = "Midfielder",
                    heightCm = 175,
                    weightKg = 70,
                    matchesPlayed = 12,
                    minutesPlayed = 950,
                    goals = 2,
                    assists = 4,
                    yellowCards = 2,
                    redCards = 0,
                    shotsPerGame = 1.1,
                    passSuccessPercentage = 88.5,
                    aerialsWonPerGame = 0.9,
                    manOfTheMatch = 0,
                    rating = 7.2,
                    id = 2
                )
            )
        )

        val result = teamService.getPlayersByTeamName(teamName)

        assertEquals(fetchedTeam.players, result)
        verify(whoScoreWebService).findPlayersByTeamName(teamName)
        verify(teamRepository).save(wantedTeam)
    }

    @Test
    fun getMatchesByTeamNameReturnsMatchesFromFootballDataService() {
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
    fun getNextMatchesByTeamNameFiltersMatchesNotPlayedYet() {
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

    @Test
    fun getPlayersByTeamNameDoesNotFetchFromFootballDataServiceIfTeamAlreadyHasPlayers() {
        val teamName = "TeamJ"
        val players = listOf("playerX", "playerY")
        val team = Team(name = teamName, apiId = 10, players = players)
        whenever(teamRepository.findByName(teamName)).thenReturn(team)

        val result = teamService.getPlayersByTeamName(teamName)

        assertEquals(players, result)
        verify(teamRepository).findByName(teamName)
        verifyNoInteractions(footballDataService)
        verifyNoMoreInteractions(teamRepository)
    }

    @Test
    fun compareTeamsReturnsTeamComparisonWithFeaturesFromRepositoryIfFound() {
        val teamAName = teamAstr
        val teamBName = teamBstr
        val teamAFeatures = TeamFeatures(teamName = teamAName, strengths = mutableListOf(Feature("Passing", "Strong")))
        val teamBFeatures = TeamFeatures(teamName = teamBName, weaknesses = mutableListOf(Feature("Defense", "Weak")))

        whenever(teamFeaturesRepository.findByTeamName(teamAName)).thenReturn(teamAFeatures)
        whenever(teamFeaturesRepository.findByTeamName(teamBName)).thenReturn(teamBFeatures)

        val result = teamService.compareTeams(teamAName, teamBName)

        assertEquals(teamAFeatures, result.teamAPerformance)
        assertEquals(teamBFeatures, result.teamBPerformance)
        verify(teamFeaturesRepository).findByTeamName(teamAName)
        verify(teamFeaturesRepository).findByTeamName(teamBName)
        verifyNoInteractions(whoScoreWebService)
    }

    @Test
    fun compareTeamsFetchesFromWhoScoreWebServiceAndSavesIfFeaturesNotFoundInRepository() {
        val teamAName = teamAstr
        val teamBName = teamBstr
        val teamAFeaturesFetched = TeamFeatures(teamName = teamAName, strengths = mutableListOf(Feature("Shooting", "Very Strong")))
        val teamBFeaturesFetched = TeamFeatures(teamName = teamBName, styleOfPlay = mutableListOf(Feature("Counter Attack")))

        whenever(teamFeaturesRepository.findByTeamName(teamAName)).thenReturn(null)
        whenever(teamFeaturesRepository.findByTeamName(teamBName)).thenReturn(null)
        whenever(whoScoreWebService.findTeamFeatures(teamAName)).thenReturn(teamAFeaturesFetched)
        whenever(whoScoreWebService.findTeamFeatures(teamBName)).thenReturn(teamBFeaturesFetched)
        whenever(teamFeaturesRepository.save(any<TeamFeatures>())).thenAnswer { it.arguments[0] as TeamFeatures }

        val result = teamService.compareTeams(teamAName, teamBName)

        assertEquals(teamAFeaturesFetched, result.teamAPerformance)
        assertEquals(teamBFeaturesFetched, result.teamBPerformance)
        verify(whoScoreWebService).findTeamFeatures(teamAName)
        verify(whoScoreWebService).findTeamFeatures(teamBName)
        verify(teamFeaturesRepository).save(teamAFeaturesFetched)
        verify(teamFeaturesRepository).save(teamBFeaturesFetched)
    }

    @Test
    fun compareTeamsFetchesOneFromWhoScoreWebServiceAndLoadsOtherFromRepository() {
        val teamAName = teamAstr
        val teamBName = teamBstr
        val teamAFeatures = TeamFeatures(teamName = teamAName, weaknesses = mutableListOf(Feature("Discipline", "Average")))
        val teamBFeaturesFetched = TeamFeatures(teamName = teamBName, strengths = mutableListOf(Feature("Set-pieces", "Little Strong")))

        whenever(teamFeaturesRepository.findByTeamName(teamAName)).thenReturn(teamAFeatures)
        whenever(teamFeaturesRepository.findByTeamName(teamBName)).thenReturn(null)
        whenever(whoScoreWebService.findTeamFeatures(teamBName)).thenReturn(teamBFeaturesFetched)
        whenever(teamFeaturesRepository.save(any<TeamFeatures>())).thenAnswer { it.arguments[0] as TeamFeatures }

        val result = teamService.compareTeams(teamAName, teamBName)

        assertEquals(teamAFeatures, result.teamAPerformance)
        assertEquals(teamBFeaturesFetched, result.teamBPerformance)
        verify(teamFeaturesRepository).findByTeamName(teamAName)
        verify(teamFeaturesRepository).findByTeamName(teamBName)
        verify(whoScoreWebService).findTeamFeatures(teamBName)
        verify(teamFeaturesRepository).save(teamBFeaturesFetched)
        verify(whoScoreWebService, never()).findTeamFeatures(teamAName)
        verify(teamFeaturesRepository, never()).save(teamAFeatures)
    }
}