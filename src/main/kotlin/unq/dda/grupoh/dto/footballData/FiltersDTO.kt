package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class FiltersDTO(
    val competitions: String? = null,
    val permission: String? = null,
    val limit: Int? = null
)