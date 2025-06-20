package unq.dda.grupoh.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.PlayerPerfomance
import unq.dda.grupoh.service.PlayerService


@RestController
@RequestMapping("/player")
class PlayerController(
    private val playerService: PlayerService
) {
    @GetMapping("/player/performance")
    fun getPlayerPerformance(
        @RequestParam("team") teamName: String,
        @RequestParam("name") playerName: String
    ): PlayerPerfomance = playerService.playerPerfomance(teamName, playerName)

    @GetMapping("/players")
    fun getAllPlayers(): List<Player> {
        return playerService.findAll()
    }
}