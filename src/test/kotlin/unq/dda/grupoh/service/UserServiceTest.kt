package unq.dda.grupoh.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import unq.dda.grupoh.model.UserAccount
import unq.dda.grupoh.repository.UserRepository
import java.lang.IllegalArgumentException

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val userService = UserService(userRepository)

    @Test
    fun authenticateReturnsTrueWhenUserExistsAndPasswordMatches() {
        val username = "user1"
        val testPwd = "pass123"
        val user = UserAccount(username, testPwd)
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        val result = userService.authenticate(username, testPwd)

        assertTrue(result)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun authenticateReturnsFalseWhenUserDoesNotExist() {
        whenever(userRepository.findByUsername(any())).thenReturn(null)

        val result = userService.authenticate("anyUser", "anyPass")

        assertFalse(result)
        verify(userRepository).findByUsername("anyUser")
    }

    @Test
    fun authenticateReturnsFalseWhenPasswordDoesNotMatch() {
        val username = "user2"
        val user = UserAccount(username, "correctPass")
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        val result = userService.authenticate(username, "wrongPass")

        assertFalse(result)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun existsReturnsTrueWhenUserRepositoryExistsByIdReturnsTrue() {
        val username = "user3"
        whenever(userRepository.existsById(username)).thenReturn(true)

        val result = userService.exists(username)

        assertTrue(result)
        verify(userRepository).existsById(username)
    }

    @Test
    fun existsReturnsFalseWhenUserRepositoryExistsByIdReturnsFalse() {
        val username = "user4"
        whenever(userRepository.existsById(username)).thenReturn(false)

        val result = userService.exists(username)

        assertFalse(result)
        verify(userRepository).existsById(username)
    }

    @Test
    fun registerSavesNewUserWithGivenUsernameAndPassword() {
        val username = "newUser"
        val testPwd = "newPass"
        whenever(userRepository.existsById(username)).thenReturn(false)
        userService.register(username, testPwd)

        val expectedUser = UserAccount(username, testPwd)
        verify(userRepository).save(check {
            assertEquals(expectedUser.username, it.username)
            assertEquals(expectedUser.password, it.password)
        })
    }

    @Test
    fun registerThrowsIllegalArgumentExceptionWhenUsernameIsAlreadyInUse() {
        val username = "newUser"
        val testPwd = "newPass"
        whenever(userRepository.existsById(username)).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            userService.register(username, testPwd)
        }
    }
}