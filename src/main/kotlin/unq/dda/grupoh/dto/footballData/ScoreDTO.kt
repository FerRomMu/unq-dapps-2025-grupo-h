package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class ScoreDTO(
    val winner: String? = null,
    val duration: String,
    val fullTime: ScoreValueDTO,
    val halfTime: ScoreValueDTO
)