package unq.dda.grupoh.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserAccount (
    @Id
    val username: String,
    @Column(nullable = false)
    val password: String
)