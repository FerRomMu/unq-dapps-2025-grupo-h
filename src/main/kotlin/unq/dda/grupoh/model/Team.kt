package unq.dda.grupoh.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "teams")
data class Team (
    @Id
    private val teamId: Int?,
    private val name: String,
    @OneToMany
    val players: List<Player> = emptyList()
)
