package unq.dda.grupoh.model

data class TeamComparision (
    val ratings: Pair<Double?, Double?>,
    val teamAPerformance: TeamPerformance,
    val teamBPerformance: TeamPerformance
)