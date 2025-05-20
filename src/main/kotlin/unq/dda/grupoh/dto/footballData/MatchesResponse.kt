package unq.dda.grupoh.dto.footballData

import kotlinx.serialization.Serializable

@Serializable
data class MatchesResponse(
    val filters: FiltersDTO,
    val resultSet: ResultSetDTO,
    val matches: List<MatchDTO>
)
