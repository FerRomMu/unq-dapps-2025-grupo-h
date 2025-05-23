package unq.dda.grupoh.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import unq.dda.grupoh.model.Team

interface TeamRepository : JpaRepository<Team, Int> {
    fun findByName(name: String): Team?
}