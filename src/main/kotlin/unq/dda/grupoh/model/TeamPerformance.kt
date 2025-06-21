package unq.dda.grupoh.model

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "team_performance",
    uniqueConstraints = [UniqueConstraint(columnNames = ["team_name"])]
)
data class TeamPerformance(
    @Column(name = "team_name", nullable = false)
    var teamName: String? = null,

    @ElementCollection
    @CollectionTable(
        name = "team_performance_tournament",
        joinColumns = [JoinColumn(name = "team_performance_id")]
    )
    var tournamentPerformances: MutableList<TournamentPerformance> = mutableListOf(),

    @Embedded
    var meanPerformance: TournamentPerformance = TournamentPerformance(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
)