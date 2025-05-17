package unq.dda.grupoh.service

import org.springframework.stereotype.Service
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.repository.ScrapRepository
import unq.dda.grupoh.repository.TeamRepository

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val scrapRepository: ScrapRepository
) {

    fun getPlayersByTeamName(teamName: String): List<Player> {
        var team = teamRepository.findByName(teamName)
        if (team == null) {
            team = scrapRepository.findByTeam(teamName)
            teamRepository.save(team)
        }
        return team.players
    }
}
