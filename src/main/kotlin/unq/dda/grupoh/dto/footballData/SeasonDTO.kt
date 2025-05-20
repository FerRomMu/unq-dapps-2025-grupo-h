package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SeasonDTO(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int? = null,
    val winner: JsonElement? = null
)