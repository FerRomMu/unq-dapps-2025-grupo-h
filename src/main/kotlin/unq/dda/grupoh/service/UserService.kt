package unq.dda.grupoh.service

import org.springframework.stereotype.Service
import unq.dda.grupoh.model.UserAccount
import unq.dda.grupoh.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun authenticate(username: String, password: String): Boolean {
        val user: UserAccount = userRepository.findByUsername(username) ?: return false
        return user.password == password
    }

    fun exists(username: String): Boolean {
        return userRepository.existsById(username)
    }

    fun register(username: String, password: String) {
        val newUser = UserAccount(username, password)
        if(exists(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        userRepository.save(newUser)
    }
}
