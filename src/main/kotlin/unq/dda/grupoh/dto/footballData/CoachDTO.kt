package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoachDTO(
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val name: String,
    @SerialName("dateOfBirth")
    val dateOfBirth: String?,
    val nationality: String?,
    val contract: CoachContractDTO?
)