package unq.dda.grupoh.model

data class TournamentPerformance(
    val tournament: String,
    val apps: Int? = null,
    val goals: Int? = null,
    val shotsPerGame: Double? = null,
    val yellowCards: Int? = null,
    val redCards: Int? = null,
    val possessionPercentage: Double? = null,
    val passSuccessPercentage: Double? = null,
    val aerialsWonPerGame: Double? = null,
    val rating: Double? = null
)