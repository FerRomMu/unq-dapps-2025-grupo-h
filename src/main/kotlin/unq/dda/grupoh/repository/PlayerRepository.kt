package unq.dda.grupoh.repository

import org.springframework.data.jpa.repository.JpaRepository
import unq.dda.grupoh.model.Player

interface PlayerRepository : JpaRepository<Player, Int> {
    fun findByNameAndTeamName(name: String, teamName: String): Player?
    fun findAllByTeamName(teamName: String): List<Player>
}