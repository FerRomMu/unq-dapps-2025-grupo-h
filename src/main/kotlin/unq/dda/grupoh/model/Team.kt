package unq.dda.grupoh.model

import jakarta.persistence.*

@Entity
@Table(name = "teams")
data class Team (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val teamId: Int? = null,
    val name: String,
    val apiId: Int,
    val players: List<String> = emptyList()
)
