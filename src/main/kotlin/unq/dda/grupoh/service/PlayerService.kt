package unq.dda.grupoh.service

import org.springframework.stereotype.Service
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.PlayerPerfomance
import unq.dda.grupoh.repository.PlayerRepository
import unq.dda.grupoh.webservice.WhoScoreWebService

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
    private val whoScoreWebService: WhoScoreWebService
) {

    fun getPlayer(teamName: String, playerName: String): Player {
        var player = playerRepository.findByNameAndTeamName(playerName, teamName)
        if (player == null) {
            val scrapedPlayers = whoScoreWebService.findPlayersByTeamName(teamName)
            val existingByName = playerRepository.findAllByTeamName(teamName).associateBy { it.name }

            val toSave = scrapedPlayers.map {
                val existing = existingByName[it.name]
                if (existing != null) {
                    it.copy(id = existing.id) // fuerza UPDATE
                } else {
                    it
                }
            }
            playerRepository.saveAll(toSave)
            player = scrapedPlayers.find { it.name == playerName } ?:
            throw ResourceNotFoundException("No hay jugador $playerName en equipo $teamName")
        }
        return player
    }

    fun playerPerfomance(teamName: String, playerName: String): PlayerPerfomance {
        val player = getPlayer(teamName, playerName)
        val matches = player.matchesPlayed?.takeIf { it > 0 } ?: 1
        return PlayerPerfomance(
            playerName = player.name,
            teamName = player.teamName,
            goalsPerGame = (player.goals ?: 0).toDouble() / matches,
            assistsPerGame = (player.assists ?: 0).toDouble() / matches,
            cardsPerGame = ((player.yellowCards ?: 0) + (player.redCards ?: 0)).toDouble() / matches,
            shotsPerGame = player.shotsPerGame ?: 0.0,
            aerialsWonPerGame = player.aerialsWonPerGame ?: 0.0,
            rating = player.rating ?: 0.0
        )
    }

    fun findAll(): List<Player> {
        return playerRepository.findAll()
    }
}