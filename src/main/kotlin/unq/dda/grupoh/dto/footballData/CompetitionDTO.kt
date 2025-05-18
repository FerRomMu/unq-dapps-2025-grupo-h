package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionDTO(
    val id: Int,
    val name: String,
    val code: String?,
    val type: String?,
    val emblem: String?
)