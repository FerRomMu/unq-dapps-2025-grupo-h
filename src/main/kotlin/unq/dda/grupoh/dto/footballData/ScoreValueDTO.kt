package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class ScoreValueDTO(
    val home: Int? = null,
    val away: Int? = null
)