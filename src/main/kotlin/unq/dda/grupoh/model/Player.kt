package unq.dda.grupoh.model

data class Player(
    val id: Int,
    val name: String,
    val position: String,
    val dateOfBirth: String,
    val nationality: String,
    val shirtNumber: Int,
    val marketValue: Int
){
}
