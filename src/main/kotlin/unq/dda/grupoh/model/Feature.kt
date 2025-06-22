package unq.dda.grupoh.model

import jakarta.persistence.Embeddable

@Embeddable
data class Feature(
    val name: String,
    val value: String? = null
)