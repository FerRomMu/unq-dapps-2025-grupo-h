package unq.dda.grupoh.repository

import org.springframework.data.jpa.repository.JpaRepository
import unq.dda.grupoh.model.TeamPerformance

interface TeamPerformanceRepository: JpaRepository<TeamPerformance, Int>{
    fun findByTeamName(name: String): TeamPerformance?
}