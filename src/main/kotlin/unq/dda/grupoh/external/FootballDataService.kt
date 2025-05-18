package unq.dda.grupoh.external

import unq.dda.grupoh.model.Team
import java.net.http.HttpClient
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import unq.dda.grupoh.dto.footballData.TeamDetailResponse
import unq.dda.grupoh.dto.footballData.TeamResponse
import unq.dda.grupoh.exceptions.ExternalErrorException
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class FootballDataService(
    @Value("\${football-data-api.token}") private val apiToken: String
) {

    private val baseUrl: String = "https://api.football-data.org/v4/"
    private var offset: Int = 0
    private val client: HttpClient = HttpClient.newBuilder().build()
    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun fetchTeam(teamName: String): List<Team> {
        val fullUrl = baseUrl + "teams"
        val limit = 500

        val allTeams = mutableListOf<Team>()
        var teamToFind: Team? = null

        while (teamToFind == null) {
            val uri = URI.create("$fullUrl?limit=$limit&offset=$offset")
            val request = HttpRequest.newBuilder()
                .uri(uri)
                .header("X-Auth-Token", apiToken)
                .build()

            val response = withContext(Dispatchers.IO) {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            }

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

            if (apiResponse.count < limit) {
                offset = 0
                break
            }

            offset += limit
        }

        return allTeams
    }

    private suspend fun fetchPlayers(teamId: Int): List<String> {
        val url = "$baseUrl/teams/$teamId"
        val uri = URI.create(url)

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header("X-Auth-Token", apiToken)
            .build()

        val response = withContext(Dispatchers.IO) {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        }

        if (response.statusCode() != 200) {
            throw ExternalErrorException("Error fetching players for team $teamId: ${response.statusCode()} - ${response.body()}")
        }

        val teamDetailResponse = json.decodeFromString<TeamDetailResponse>(response.body())
        return teamDetailResponse.squad?.map { it.name } ?: emptyList()
    }

    suspend fun findByName(teamName: String): Pair<Team?, List<Team>> {
        val allTeams = fetchTeam(teamName)
        var team = allTeams.find { it.name.equals(teamName, ignoreCase = true) }
        if(team != null) {
            val players = fetchPlayers(team.apiId)
            team = team.copy(players = players)
        }
        return Pair(team, allTeams)
    }

    suspend fun findById(apiId: Int, name: String): Team = Team(
        null,
        name,
        apiId,
        fetchPlayers(apiId)
    )
}
