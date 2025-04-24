package unq.dda.grupoh.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.call.body
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import unq.dda.grupoh.model.Player

class TeamService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            serialization(ContentType.Application.Json, Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getPlayersByTeamName(teamName: String): List<Player> {
        val response: Map<String, Any> = client.get("https://api.football-data.org/v4/teams") {
            headers {
                append(HttpHeaders.Authorization, "X-Auth-Token una_token_segura") // TODO VALIDAR TOKEN
            }
        }.body()

        // Filtrar el equipo por nombre
        val teams = response["teams"] as List<Map<String, Any>>
        val team = teams.firstOrNull { (it["name"] as String).contains(teamName, ignoreCase = true) }

        return if (team != null) {
            // Obtener detalles del equipo específico
            val teamId = team["id"] as Int
            val teamDetails: Map<String, Any> = client.get("https://api.football-data.org/v4/teams/$teamId") {
                headers {
                    append(HttpHeaders.Authorization, "X-Auth-Token una_token_segura")
                }
            }.body()

            // Extraer jugadores del equipo
            val squad = teamDetails["squad"] as List<Map<String, Any>>

            squad.map {
                Player(
                    id = it["id"] as Int,
                    name = it["name"] as String,
                    position = it["position"] as String,
                    dateOfBirth = it["¨dateOfBirth"] as String,
                    nationality = it["nationality"] as String,
                    shirtNumber = it["shirtNumber"] as Int,
                    marketValue = it["marketValue"] as Int,
                )
            }
        } else {
            emptyList()
        }
    }

}
