package unq.dda.grupoh.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.PlayerPerfomance
import unq.dda.grupoh.service.PlayerService

class PlayerControllerTest {

    private lateinit var playerService: PlayerService
    private lateinit var playerController: PlayerController

    @BeforeEach
    fun setUp() {
        playerService = mock()
        playerController = PlayerController(playerService)
    }

    @Test
    fun getPlayerPerformanceShouldCallServiceAndReturnPlayerPerfomance() {
        val teamName = "Barcelona"
        val playerName = "Lionel Messi"
        val expectedPerformance = PlayerPerfomance(
            playerName = playerName,
            teamName = teamName,
            goalsPerGame = 0.8,
            assistsPerGame = 0.5,
            cardsPerGame = 0.1,
            shotsPerGame = 3.0,
            aerialsWonPerGame = 0.2,
            rating = 9.5
        )

        whenever(playerService.playerPerfomance(teamName, playerName))
            .thenReturn(expectedPerformance)

        val result = playerController.getPlayerPerformance(teamName, playerName)

        assertEquals(expectedPerformance, result)

        verify(playerService).playerPerfomance(teamName, playerName)

        verifyNoMoreInteractions(playerService)
    }

    @Test
    fun getAllPlayersShouldCallServiceAndReturnListOfPlayers() {
        val expectedPlayers = listOf(
            Player(id = 1, name = "Player One", teamName = "Team A"),
            Player(id = 2, name = "Player Two", teamName = "Team B")
        )

        whenever(playerService.findAll())
            .thenReturn(expectedPlayers)

        val result = playerController.getAllPlayers()

        assertEquals(expectedPlayers.size, result.size)
        assertEquals(expectedPlayers[0], result[0])
        assertEquals(expectedPlayers[1], result[1])

        verify(playerService).findAll()
        verifyNoMoreInteractions(playerService)
    }

    @Test
    fun getAllPlayersShouldReturnEmptyListIfServiceReturnsEmpty() {
        val expectedPlayers = emptyList<Player>()

        whenever(playerService.findAll())
            .thenReturn(expectedPlayers)

        val result = playerController.getAllPlayers()

        assertTrue(result.isEmpty())

        verify(playerService).findAll()
        verifyNoMoreInteractions(playerService)
    }
}