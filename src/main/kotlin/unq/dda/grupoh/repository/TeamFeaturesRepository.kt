package unq.dda.grupoh.repository

import org.springframework.data.jpa.repository.JpaRepository
import unq.dda.grupoh.model.TeamFeatures

interface TeamFeaturesRepository: JpaRepository<TeamFeatures, Int> {
    fun findByTeamName(name: String): TeamFeatures?
}