package unq.dda.grupoh.repository

import org.springframework.data.jpa.repository.JpaRepository
import unq.dda.grupoh.model.UserAccount

interface UserRepository : JpaRepository<UserAccount, String> {
    fun findByUsername(username: String): UserAccount?
}
