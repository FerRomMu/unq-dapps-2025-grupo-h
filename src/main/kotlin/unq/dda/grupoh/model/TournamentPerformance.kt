package unq.dda.grupoh.model

import jakarta.persistence.Embeddable

@Embeddable
data class TournamentPerformance(
    var tournament: String? = null,
    var apps: Int? = null,
    var goals: Int? = null,
    var shotsPerGame: Double? = null,
    var yellowCards: Int? = null,
    var redCards: Int? = null,
    var possessionPercentage: Double? = null,
    var passSuccessPercentage: Double? = null,
    var aerialsWonPerGame: Double? = null,
    var rating: Double? = null
)