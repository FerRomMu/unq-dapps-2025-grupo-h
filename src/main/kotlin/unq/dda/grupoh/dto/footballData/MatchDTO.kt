package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class MatchDTO(
    val area: AreaDTO,
    val competition: CompetitionDTO,
    val season: SeasonDTO,
    val id: Int,
    val utcDate: String,
    val status: String,
    val matchday: Int? = null,
    val stage: String,
    val group: String? = null,
    val lastUpdated: String,
    val homeTeam: TeamInfoDTO,
    val awayTeam: TeamInfoDTO,
    val score: ScoreDTO,
    val odds: OddsDTO? = null,
    val referees: List<RefereeDTO>? = emptyList()
)