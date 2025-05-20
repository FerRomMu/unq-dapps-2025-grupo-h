package unq.dda.grupoh.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MatchTest {

    @Test
    fun `isNotPlayedYet returns true when status is SCHEDULED`() {
        val match = Match(
            id = 1,
            date = "2025-05-01",
            status = "SCHEDULED",
            stage = "GROUP",
            homeTeam = "TeamA",
            awayTeam = "TeamB",
            scoreHome = null,
            scoreAway = null,
            winner = null
        )

        assertTrue(match.isNotPlayedYet())
    }

    @Test
    fun `isNotPlayedYet returns false when status is not SCHEDULED`() {
        val statuses = listOf("LIVE","IN_PLAY","PAUSED","FINISHED","POSTPONED","SUSPENDED","CANCELLED")
        statuses.forEach { status ->
            val match = Match(
                id = 2,
                date = "2025-05-01",
                status = status,
                stage = "GROUP",
                homeTeam = "TeamA",
                awayTeam = "TeamB",
                scoreHome = 1,
                scoreAway = 0,
                winner = "TeamA"
            )
            assertFalse(match.isNotPlayedYet(), "Expected false for status: $status")
        }
    }
}