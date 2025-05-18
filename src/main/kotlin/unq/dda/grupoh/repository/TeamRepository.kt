package unq.dda.grupoh.repository

import org.springframework.data.jpa.repository.JpaRepository
import unq.dda.grupoh.model.Team

interface TeamRepository : JpaRepository<Team, Int> {
    fun findByName(name: String): Team?
}