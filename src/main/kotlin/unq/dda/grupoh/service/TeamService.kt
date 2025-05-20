package unq.dda.grupoh.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.external.FootballDataService
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.repository.TeamRepository
import org.springframework.transaction.annotation.Transactional

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val footballDataService: FootballDataService
) {

    @Transactional
    fun saveOrUpdateByName(team: Team): Team {
        val updated = teamRepository.updateByName(team)
        return if (updated == 0) teamRepository.save(team)
        else teamRepository.findByName(team.name)!!
    }

    fun getTeam(teamName: String): Team {
        return teamRepository.findByName(teamName)
            ?: run {
                val (mainTeam, allTeams) = runBlocking { footballDataService.findTeamByName(teamName) }
                allTeams.forEach { saveOrUpdateByName(it) }
                mainTeam ?: throw ResourceNotFoundException("Team not found.")
            }
    }

    fun getPlayersByTeamName(teamName: String): List<String> {
        var team = getTeam(teamName)

        if (team.players.isEmpty()) {
            team = runBlocking { footballDataService.findTeamById(team.apiId, team.name) }
            saveOrUpdateByName(team)
        }

        return team.players
    }

    fun getMatchesByTeamName(teamName: String): List<Match> {
        val team = getTeam(teamName)
        return footballDataService.findMatchesByTeam(team)
    }

    fun getNextMatchesByTeamName(teamName: String): List<Match> {
        return getMatchesByTeamName(teamName).filter { it.isNotPlayedYet() }
    }
}
