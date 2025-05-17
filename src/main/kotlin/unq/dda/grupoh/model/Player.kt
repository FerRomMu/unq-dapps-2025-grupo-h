package unq.dda.grupoh.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "players")
data class Player(
    @Id
    val id: Int,
    val name: String,
    val position: String,
    val dateOfBirth: String,
    val nationality: String,
    val shirtNumber: Int,
    val marketValue: Int
)
