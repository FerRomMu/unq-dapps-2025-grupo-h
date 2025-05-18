package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDetailResponse(
    val area: AreaDTO,
    val id: Int,
    val name: String?,
    @SerialName("shortName")
    val shortName: String?,
    val tla: String?,
    val crest: String?,
    val address: String?,
    val website: String?,
    val founded: Int?,
    @SerialName("clubColors")
    val clubColors: String?,
    val venue: String?,
    @SerialName("runningCompetitions")
    val runningCompetitions: List<CompetitionDTO>?,
    val coach: CoachDTO?,
    val squad: List<PlayerDTO>?,
    val staff: List<StaffDTO>?,
    val lastUpdated: String?
)