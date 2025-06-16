package unq.dda.grupoh.webservice

import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import unq.dda.grupoh.dto.footballData.MatchesResponse
import unq.dda.grupoh.dto.footballData.TeamDetailResponse
import unq.dda.grupoh.dto.footballData.TeamResponse
import unq.dda.grupoh.exceptions.ExternalErrorException
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.model.Team
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.slf4j.LoggerFactory
import org.slf4j.Logger

@Service
class FootballDataService(
    @Value("\${football-data-api.token}") private val apiToken: String,
    @Value("\${logging.webservice.verbose:false}") private val verboseLogging: String,
    private val client: HttpClient = HttpClient.newBuilder().build(),
    private val logger: Logger = LoggerFactory.getLogger(FootballDataService::class.java)
) {

    private val baseUrl: String = "https://api.football-data.org/v4/"
    private var offset: Int = 0
    private val json = Json { ignoreUnknownKeys = true }

    private fun fetch(url: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .header("X-Auth-Token", apiToken)
            .build()

        if (verboseLogging.toBoolean()) {
            logger.info("LOGGER - OUTGOING REQUEST: ${request.method()} ${request.uri()}")
        }

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (verboseLogging.toBoolean()) {
            val rawBody = response.body()
            val prettyJson = try {
                Json { prettyPrint = true }.parseToJsonElement(rawBody).toString()
            } catch (e: Exception) {
                rawBody
            }

            logger.info("LOGGER - RESPONSE [${response.statusCode()}]:\n $prettyJson")
        }

        return response
    }

    private fun fetchTeam(teamName: String): List<Team> {
        val fullUrl = baseUrl + "teams"
        val limit = 500

        val allTeams = mutableListOf<Team>()
        var teamToFind: Team? = null

        while (teamToFind == null) {
            val response = fetch("$fullUrl?limit=$limit&offset=$offset")

            if (response.statusCode() != 200) {
                print("Error at API call: ${response.statusCode()} - ${response.body()}")
                break
            }

            val apiResponse = json.decodeFromString<TeamResponse>(response.body())
            val currentTeams = apiResponse.teams.map {
                Team(name = it.shortName ?: it.name!!, apiId = it.id)
            }
            allTeams.addAll(currentTeams)

            teamToFind = currentTeams.find { it.name.equals(teamName, ignoreCase = true) }
            logger.info("Equipo -- $teamToFind")

            if (apiResponse.count < limit) {
                offset = 0
                break
            }

            offset += limit
        }

        return allTeams
    }

    private fun fetchPlayers(teamId: Int): List<String> {
        val response = fetch("$baseUrl/teams/$teamId")

        if (response.statusCode() != 200) {
            throw ExternalErrorException("Error fetching players for team $teamId: ${response.statusCode()} - ${response.body()}")
        }

        val teamDetailResponse = json.decodeFromString<TeamDetailResponse>(response.body())
        return teamDetailResponse.squad?.map { it.name } ?: emptyList()
    }

    fun fetchMatches(teamName: String, teamId: Int): List<Match> {
        val response = fetch("$baseUrl/teams/$teamId/matches")

        if (response.statusCode() != 200) {
            throw ExternalErrorException("Error fetching matches for team $teamId: ${response.statusCode()} - ${response.body()}")
        }

        val matchesResponse = json.decodeFromString<MatchesResponse>(response.body())

        return matchesResponse.matches.map {
            Match(
                id = it.id,
                date = it.utcDate,
                status = it.status,
                stage = it.stage,
                homeTeam = it.homeTeam.name,
                awayTeam = it.awayTeam.name,
                scoreHome = it.score.fullTime.home,
                scoreAway = it.score.fullTime.away,
                winner = it.score.winner
            )
        }
    }

    fun findTeamByName(teamName: String): Pair<Team?, List<Team>> {
        val allTeams = fetchTeam(teamName)
        var team = allTeams.find { it.name.equals(teamName, ignoreCase = true) }
        if(team != null) {
            val players = fetchPlayers(team.apiId)
            team = team.copy(players = players)
        }
        return Pair(team, allTeams)
    }

    fun findTeamById(apiId: Int, name: String): Team =
        Team(
            null,
            name,
            apiId,
            fetchPlayers(apiId)
        )

    fun findMatchesByTeam(team: Team): List<Match> =
        fetchMatches(team.name, team.apiId)
}
