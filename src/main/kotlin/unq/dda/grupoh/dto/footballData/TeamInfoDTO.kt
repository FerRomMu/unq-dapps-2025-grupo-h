package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class TeamInfoDTO(
    val id: Int,
    val name: String,
    val shortName: String? = null,
    val tla: String? = null,
    val crest: String? = null
)