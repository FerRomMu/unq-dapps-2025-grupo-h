package unq.dda.grupoh.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "players", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "team_name"])])
data class Player(
    val name: String,
    val teamName: String,
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)
