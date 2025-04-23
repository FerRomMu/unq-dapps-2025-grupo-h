package unq.dda.grupoh.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import unq.dda.grupoh.dto.PlayerDTO
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.service.TeamService

@RestController
@RequestMapping("/team")
class TeamsController(
    private val teamService: TeamService
) {

    @GetMapping("/players")
    fun getPlayersByTeam(@RequestParam("name") teamName: String): List<PlayerDTO> {
        val players: List<Player> = teamService.getPlayersByTeamName(teamName)
        return players.map { PlayerDTO(it) }
    }
}
