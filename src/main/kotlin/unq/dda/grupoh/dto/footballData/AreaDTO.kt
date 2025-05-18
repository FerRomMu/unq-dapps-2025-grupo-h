package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class AreaDTO(
    val id: Int,
    val name: String,
    val code: String?,
    val flag: String?
)