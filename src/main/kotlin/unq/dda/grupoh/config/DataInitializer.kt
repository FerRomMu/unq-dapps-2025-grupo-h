package unq.dda.grupoh.config

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import unq.dda.grupoh.model.Team
import unq.dda.grupoh.model.UserAccount
import unq.dda.grupoh.model.factory.TeamFactory
import unq.dda.grupoh.repository.TeamRepository
import unq.dda.grupoh.repository.UserRepository

@Component
class DataInitializer(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val teams: List<Team> = TeamFactory().createInitialTeams()

        val user1 = UserAccount(username = "admin", password = "admin")
        val user2 = UserAccount(username = "user", password = "1234")

        teamRepository.saveAll(teams)
        userRepository.saveAll(listOf(user1, user2))
    }
}
