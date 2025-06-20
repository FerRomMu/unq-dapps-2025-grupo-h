package unq.dda.grupoh.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.repository.PlayerRepository
import unq.dda.grupoh.webservice.WhoScoreWebService

class PlayerServiceTest {

    private lateinit var playerRepository: PlayerRepository
    private lateinit var whoScoreWebService: WhoScoreWebService
    private lateinit var playerService: PlayerService
    val messi = "Lionel Messi"
    val psg = "PSG"
    val mbappe = "Kylian Mbappe"
    val neymar = "Neymar Jr"
    val teamstr = "Team"
    val teama = "Team A"
    val teamb = "Team B"
    val teamc = "Team C"
    val playera = "Player A"
    val playerb = "Player B"
    val playerc = "Player C"

    @BeforeEach
    fun setUp() {
        playerRepository = mock()
        whoScoreWebService = mock()
        playerService = PlayerService(playerRepository, whoScoreWebService)
    }

    @Test
    fun getPlayerShouldReturnPlayerFromRepositoryIfExists() {
        val existingPlayer = Player(id = 1, name = messi, teamName = psg)
        whenever(playerRepository.findByNameAndTeamName(messi, psg))
            .thenReturn(existingPlayer)

        val result = playerService.getPlayer(psg, messi)

        assertEquals(existingPlayer, result)
        verify(whoScoreWebService, never()).findPlayersByTeamName(any())
        verify(playerRepository, never()).saveAll<Player>(any())
    }

    @Test
    fun getPlayerShouldScrapeAndSavePlayerIfNotInRepository() {
        whenever(playerRepository.findByNameAndTeamName(mbappe, psg))
            .thenReturn(null)
        whenever(playerRepository.findAllByTeamName(psg))
            .thenReturn(emptyList())

        val scrapedMbappe = Player(name = mbappe, teamName = psg, goals = 10)
        val scrapedNeymar = Player(name = neymar, teamName = psg, goals = 5)
        whenever(whoScoreWebService.findPlayersByTeamName(psg))
            .thenReturn(listOf(scrapedMbappe, scrapedNeymar))

        val savedPlayersCaptor = argumentCaptor<List<Player>>()

        val result = playerService.getPlayer(psg, mbappe)

        assertEquals(scrapedMbappe, result)
        verify(whoScoreWebService).findPlayersByTeamName(psg)
        verify(playerRepository).saveAll(savedPlayersCaptor.capture())
        val savedPlayers = savedPlayersCaptor.firstValue
        assertTrue(savedPlayers.containsAll(listOf(scrapedMbappe, scrapedNeymar)))
        assertNull(savedPlayers.find { it.name == mbappe }?.id)
        assertNull(savedPlayers.find { it.name == neymar }?.id)
    }

    @Test
    fun getPlayerShouldScrapeAndUpdateExistingPlayerIfNameMatchesButNotFoundInitially() {
        val existingNeymarInDB = Player(id = 123, name = neymar, teamName = psg, goals = 3)

        whenever(playerRepository.findByNameAndTeamName(mbappe, psg))
            .thenReturn(null)
        whenever(playerRepository.findAllByTeamName(psg))
            .thenReturn(listOf(existingNeymarInDB))

        val scrapedMbappe = Player(name = mbappe, teamName = psg, goals = 10)
        val scrapedNeymar = Player(name = neymar, teamName = psg, goals = 5) // Updated data for Neymar
        whenever(whoScoreWebService.findPlayersByTeamName(psg))
            .thenReturn(listOf(scrapedMbappe, scrapedNeymar))

        val savedPlayersCaptor = argumentCaptor<List<Player>>()

        val result = playerService.getPlayer(psg, mbappe)

        assertEquals(scrapedMbappe, result)
        verify(whoScoreWebService).findPlayersByTeamName(psg)
        verify(playerRepository).saveAll(savedPlayersCaptor.capture())

        val savedPlayers = savedPlayersCaptor.firstValue
        assertTrue(savedPlayers.contains(scrapedMbappe))
        assertNull(savedPlayers.find { it.name == mbappe }?.id)

        val updatedNeymar = savedPlayers.find { it.name == neymar }
        assertNotNull(updatedNeymar)
        assertEquals(existingNeymarInDB.id, updatedNeymar?.id)
        assertEquals(5, updatedNeymar?.goals)
    }

    @Test
    fun getPlayerShouldThrowResourceNotFoundExceptionIfPlayerNotFoundEvenAfterScraping() {
        whenever(playerRepository.findByNameAndTeamName("NonExistent", teamstr))
            .thenReturn(null)
        whenever(playerRepository.findAllByTeamName(teamstr))
            .thenReturn(emptyList())
        val otherPlayer = Player(name = "Other Player", teamName = teamstr)
        whenever(whoScoreWebService.findPlayersByTeamName(teamstr))
            .thenReturn(listOf(otherPlayer))

        val exception = assertThrows<ResourceNotFoundException> {
            playerService.getPlayer(teamstr, "NonExistent")
        }

        assertEquals("No hay jugador NonExistent en equipo Team", exception.message)
        verify(whoScoreWebService).findPlayersByTeamName(teamstr)
        verify(playerRepository).saveAll(listOf(otherPlayer))
    }

    @Test
    fun getPlayerShouldScrapeAndSaveEmptyListIfWebServiceReturnsNoPlayers() {
        whenever(playerRepository.findByNameAndTeamName("NonExistent", teamstr))
            .thenReturn(null)
        whenever(playerRepository.findAllByTeamName(teamstr))
            .thenReturn(emptyList())
        whenever(whoScoreWebService.findPlayersByTeamName(teamstr))
            .thenReturn(emptyList())

        val exception = assertThrows<ResourceNotFoundException> {
            playerService.getPlayer(teamstr, "NonExistent")
        }

        assertEquals("No hay jugador NonExistent en equipo Team", exception.message)
        verify(whoScoreWebService).findPlayersByTeamName(teamstr)
        verify(playerRepository).saveAll(emptyList())
    }

    @Test
    fun playerPerfomanceShouldCalculateCorrectlyWithFullData() {
        val player = Player(
            id = 1,
            name = "Test Player",
            teamName = "Test Team",
            matchesPlayed = 20,
            goals = 10,
            assists = 5,
            yellowCards = 2,
            redCards = 1,
            shotsPerGame = 2.5,
            aerialsWonPerGame = 1.8,
            rating = 7.5
        )
        whenever(playerRepository.findByNameAndTeamName("Test Player", "Test Team"))
            .thenReturn(player)

        val performance = playerService.playerPerfomance("Test Team", "Test Player")

        assertEquals("Test Player", performance.playerName)
        assertEquals("Test Team", performance.teamName)
        assertEquals(0.5, performance.goalsPerGame, 0.001) // 10/20
        assertEquals(0.25, performance.assistsPerGame, 0.001) // 5/20
        assertEquals(0.15, performance.cardsPerGame, 0.001) // (2+1)/20 = 3/20
        assertEquals(2.5, performance.shotsPerGame, 0.001)
        assertEquals(1.8, performance.aerialsWonPerGame, 0.001)
        assertEquals(7.5, performance.rating, 0.001)

        verify(playerRepository).findByNameAndTeamName("Test Player", "Test Team")
    }

    @Test
    fun playerPerfomanceShouldHandleNullOrZeroMatchesPlayedAsOne() {
        // Case 1: matchesPlayed is null
        val playerNullMatches = Player(
            id = 2, name = playera, teamName = teama, matchesPlayed = null,
            goals = 10, assists = 5, yellowCards = 2, redCards = 0,
            shotsPerGame = 1.0, aerialsWonPerGame = 1.0, rating = 6.0
        )
        whenever(playerRepository.findByNameAndTeamName(playera, teama))
            .thenReturn(playerNullMatches)

        val performanceNullMatches = playerService.playerPerfomance(teama, playera)
        assertEquals(10.0, performanceNullMatches.goalsPerGame, 0.001) // 10/1
        assertEquals(5.0, performanceNullMatches.assistsPerGame, 0.001) // 5/1
        assertEquals(2.0, performanceNullMatches.cardsPerGame, 0.001) // 2/1

        // Case 2: matchesPlayed is 0
        val playerZeroMatches = Player(
            id = 3, name = playerb, teamName = teamb, matchesPlayed = 0,
            goals = 10, assists = 5, yellowCards = 2, redCards = 0,
            shotsPerGame = 1.0, aerialsWonPerGame = 1.0, rating = 6.0
        )
        whenever(playerRepository.findByNameAndTeamName(playerb, teamb))
            .thenReturn(playerZeroMatches)

        val performanceZeroMatches = playerService.playerPerfomance(teamb, playerb)
        assertEquals(10.0, performanceZeroMatches.goalsPerGame, 0.001) // 10/1
        assertEquals(5.0, performanceZeroMatches.assistsPerGame, 0.001) // 5/1
        assertEquals(2.0, performanceZeroMatches.cardsPerGame, 0.001) // 2/1
    }

    @Test
    fun `playerPerfomance should handle null stats as zero`() {
        val player = Player(
            id = 4, name = playerc, teamName = teamc, matchesPlayed = 10,
            goals = null, assists = null, yellowCards = null, redCards = null,
            shotsPerGame = null, aerialsWonPerGame = null, rating = null
        )
        whenever(playerRepository.findByNameAndTeamName(playerc, teamc))
            .thenReturn(player)

        val performance = playerService.playerPerfomance(teamc, playerc)

        assertEquals(0.0, performance.goalsPerGame, 0.001)
        assertEquals(0.0, performance.assistsPerGame, 0.001)
        assertEquals(0.0, performance.cardsPerGame, 0.001)
        assertEquals(0.0, performance.shotsPerGame, 0.001)
        assertEquals(0.0, performance.aerialsWonPerGame, 0.001)
        assertEquals(0.0, performance.rating, 0.001)
    }

    @Test
    fun `playerPerfomance should throw exception if getPlayer cannot find player`() {
        val exceptionMessage = "No hay jugador NonExistent en equipo Team"

        whenever(playerRepository.findByNameAndTeamName("NonExistent", teamstr))
            .thenReturn(null)
        whenever(playerRepository.findAllByTeamName(teamstr))
            .thenReturn(emptyList())
        whenever(whoScoreWebService.findPlayersByTeamName(teamstr))
            .thenReturn(emptyList())

        val exception = assertThrows<ResourceNotFoundException> {
            playerService.playerPerfomance(teamstr, "NonExistent")
        }

        assertEquals(exceptionMessage, exception.message)
        // Verify interactions that lead to the exception
        verify(playerRepository).findByNameAndTeamName("NonExistent", teamstr)
        verify(whoScoreWebService).findPlayersByTeamName(teamstr)
    }

    @Test
    fun `findAll should return all players from repository`() {
        val players = listOf(
            Player(id = 1, name = "Player 1", teamName = teama),
            Player(id = 2, name = "Player 2", teamName = teamb)
        )
        whenever(playerRepository.findAll()).thenReturn(players)

        val result = playerService.findAll()

        assertEquals(2, result.size)
        assertTrue(result.containsAll(players))
        verify(playerRepository).findAll()
    }

    @Test
    fun `findAll should return empty list if repository is empty`() {
        whenever(playerRepository.findAll()).thenReturn(emptyList())

        val result = playerService.findAll()

        assertTrue(result.isEmpty())
        verify(playerRepository).findAll()
    }
}