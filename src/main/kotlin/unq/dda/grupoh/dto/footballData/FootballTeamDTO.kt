package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FootballTeamDTO (
    val id: Int,
    val name: String?,
    @SerialName("shortName")
    val shortName: String?,
    val tla: String?,
    val crest: String?,
    val address: String?,
    val website: String?,
    val founded: Int?,
    val clubColors: String?,
    val venue: String?,
    val lastUpdated: String?
)
