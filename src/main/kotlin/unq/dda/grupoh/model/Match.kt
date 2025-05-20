package unq.dda.grupoh.model

data class Match(
    val id: Int,
    val date: String,
    val status: String,
    val stage: String,
    val homeTeam: String,
    val awayTeam: String,
    val scoreHome: Int?,
    val scoreAway: Int?,
    val winner: String?
)
