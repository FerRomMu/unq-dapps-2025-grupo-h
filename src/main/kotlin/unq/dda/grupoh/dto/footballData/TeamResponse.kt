package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class TeamResponse (
    val count: Int,
    val filters: ApiFiltersDTO,
    val teams: List<FootballTeamDTO>
)