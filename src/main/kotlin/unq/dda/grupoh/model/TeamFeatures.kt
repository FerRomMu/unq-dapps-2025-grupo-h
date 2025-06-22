package unq.dda.grupoh.model

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn

@Entity
data class TeamFeatures(
    @Column(unique = true)
    var teamName: String? = null,

    @ElementCollection
    @CollectionTable(name = "team_strengths", joinColumns = [JoinColumn(name = "team_id")])
    var strengths: MutableList<Feature> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "team_weaknesses", joinColumns = [JoinColumn(name = "team_id")])
    var weaknesses: MutableList<Feature> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "team_styles", joinColumns = [JoinColumn(name = "team_id")])
    var styleOfPlay: MutableList<Feature> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null
)