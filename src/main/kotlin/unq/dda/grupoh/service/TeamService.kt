package unq.dda.grupoh.service

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.webservice.FootballDataWebService
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.model.TeamComparision
import unq.dda.grupoh.repository.TeamFeaturesRepository
import unq.dda.grupoh.repository.TeamRepository
import unq.dda.grupoh.webservice.WhoScoreWebService

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val teamFeaturesRepository: TeamFeaturesRepository,
    private val footballDataService: FootballDataWebService,
    private val whoScoreWebService: WhoScoreWebService
) {

    @Transactional
    private fun saveOrUpdateByName(team: Team): Team {
        val existing = teamRepository.findByName(team.name)
        return if (existing == null) {
            teamRepository.save(team)
        } else {
            if (existing.players.isEmpty()) existing.players = team.players
            teamRepository.save(existing)
        }
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
        val team = getTeam(teamName)

        if (team.players.isEmpty()) {
            team.players = whoScoreWebService.findPlayersByTeamName(teamName).map { it.name }
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

    fun compareTeams(teamA: String, teamB: String): TeamComparision {
        val teamAPerformance = teamFeaturesRepository.findByTeamName(teamA) ?:
        whoScoreWebService.findTeamFeatures(teamA).also {
            teamFeaturesRepository.save(it)
        }
        val teamBPerformance = teamFeaturesRepository.findByTeamName(teamB) ?:
        whoScoreWebService.findTeamFeatures(teamB).also {
            teamFeaturesRepository.save(it)
        }

        return TeamComparision(
            teamAPerformance,
            teamBPerformance
        )
    }
}
