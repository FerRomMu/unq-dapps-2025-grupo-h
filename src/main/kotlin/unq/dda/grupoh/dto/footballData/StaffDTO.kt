package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffDTO(
    val id: Int,
    val name: String,
    val position: String?,
    @SerialName("dateOfBirth")
    val dateOfBirth: String?,
    val nationality: String?
)