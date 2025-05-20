package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class ResultSetDTO(
    val count: Int,
    val competitions: String? = null,
    val first: String,
    val last: String,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int
)