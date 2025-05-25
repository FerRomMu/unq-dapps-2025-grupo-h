package unq.dda.grupoh.model

import jakarta.persistence.*

@Entity
@Table(name = "teams", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
data class Team (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val teamId: Int? = null,
    @Column(nullable = false, unique = true)
    val name: String,
    @Column(nullable = false)
    val apiId: Int,
    var players: List<String> = emptyList()
)
