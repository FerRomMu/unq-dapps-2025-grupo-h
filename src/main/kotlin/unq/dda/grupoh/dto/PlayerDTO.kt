package unq.dda.grupoh.dto

import unq.dda.grupoh.model.Player

data class PlayerDTO(
    val name: String,
    val age: Int,
    val team: String,
    val nationality: String,
    val height: Double,
    val positions: List<String>
) {
    constructor(player: Player) : this(
        name = player.name,
        age = player.age,
        team = player.team,
        nationality = player.nationality,
        height = player.height,
        positions = player.positions
    )
}
