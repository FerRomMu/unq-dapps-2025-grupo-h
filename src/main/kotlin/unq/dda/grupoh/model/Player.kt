package unq.dda.grupoh.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "players")
data class Player(
    val name: String,
    val age: Int?,
    val position: String?,
    val heightCm: Int?,
    val weightKg: Int?,
    val matchesPlayed: Int?,
    val minutesPlayed: Int?,
    val goals: Int?,
    val assists: Int?,
    val yellowCards: Int?,
    val redCards: Int?,
    val shotsPerGame: Double?,
    val passSuccessPercentage: Double?,
    val aerialsWonPerGame: Double?,
    val manOfTheMatch: Int?,
    val rating: Double?,
    @Id
    val id: Int? = null
)
