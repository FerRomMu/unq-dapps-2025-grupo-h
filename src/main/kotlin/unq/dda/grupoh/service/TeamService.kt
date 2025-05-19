package unq.dda.grupoh.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.external.FootballDataService
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.repository.TeamRepository

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val footballDataService: FootballDataService
) {

    fun getPlayersByTeamName(teamName: String): List<String> {

        var team: Team = teamRepository.findByName(teamName) ?: run {
            val teams: Pair<Team?, List<Team>> = runBlocking { footballDataService.findByName(teamName) }
            teamRepository.saveAll(teams.second)
            teams.first ?: throw ResourceNotFoundException("Team not found.")
        }.also { teamRepository.save(it) }

        if (team.players.isEmpty()) {
            team = runBlocking { footballDataService.findById(team.apiId, team.name) }
            teamRepository.save(team)
        }

        return team.players
    }

    fun getMatchesByTeamName(teamName: String): List<Match> {
        TODO("Not yet implemented")
    }
}
