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
    val age: Int? = null,
    val position: String? = null,
    val heightCm: Int? = null,
    val weightKg: Int? = null,
    val matchesPlayed: Int? = null,
    val minutesPlayed: Int? = null,
    val goals: Int? = null,
    val assists: Int? = null,
    val yellowCards: Int? = null,
    val redCards: Int? = null,
    val shotsPerGame: Double? = null,
    val passSuccessPercentage: Double? = null,
    val aerialsWonPerGame: Double? = null,
    val manOfTheMatch: Int? = null,
    val rating: Double? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null
)
