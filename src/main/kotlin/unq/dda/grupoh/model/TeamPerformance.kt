package unq.dda.grupoh.model

data class TeamPerformance(
    val teamName: String,
    val tournamentPerformances: List<TournamentPerformance>,
    val meanPerformance: TournamentPerformance
)