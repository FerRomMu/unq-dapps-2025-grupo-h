package unq.dda.grupoh.model

data class PlayerPerfomance (
    val playerName: String,
    val teamName: String,
    val goalsPerGame: Double,
    val assistsPerGame: Double,
    val cardsPerGame: Double,
    val shotsPerGame: Double,
    val aerialsWonPerGame: Double,
    val rating: Double
)