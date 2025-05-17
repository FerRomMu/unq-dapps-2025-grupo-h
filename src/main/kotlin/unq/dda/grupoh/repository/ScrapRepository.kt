package unq.dda.grupoh.repository

import org.springframework.stereotype.Repository
import unq.dda.grupoh.model.Team

@Repository
class ScrapRepository {

    fun findByTeam(teamName: String): Team {
        // TODO
        return Team(null, "a team")
    }
}
