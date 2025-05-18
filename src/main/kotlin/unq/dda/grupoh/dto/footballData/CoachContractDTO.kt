package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class CoachContractDTO(
    val start: String?,
    val until: String?
)