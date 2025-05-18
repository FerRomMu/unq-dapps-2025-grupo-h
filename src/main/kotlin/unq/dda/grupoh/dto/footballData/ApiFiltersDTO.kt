package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class ApiFiltersDTO(
    val limit: Int,
    val offset: Int,
    val permission: String
)
