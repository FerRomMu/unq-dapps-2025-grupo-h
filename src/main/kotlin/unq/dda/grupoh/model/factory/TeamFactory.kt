package unq.dda.grupoh.model.factory

import unq.dda.grupoh.model.Team

class TeamFactory {
    private val playerLetters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K")

    fun createInitialTeams(count: Int = 10): List<Team> {
        fun createTeam(name: String, apiId: Int, players: List<String>): Team {
            return Team(name = name, apiId = apiId, players = players)
        }

        return (1..count).map { index ->
            createTeam(
                name = "Equipo $index",
                apiId = 0,
                players = playerLetters.map { letter -> "Jugador ${index}$letter" }
            )
        }
    }
}