package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class RefereeDTO(
    val id: Int,
    val name: String,
    val type: String,
    val nationality: String
)