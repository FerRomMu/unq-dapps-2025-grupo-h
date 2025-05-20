package unq.dda.grupoh.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import unq.dda.grupoh.model.UserAccount
import unq.dda.grupoh.repository.UserRepository

class UserServiceTest {

    private val userRepository: UserRepository = mock()
    private val userService = UserService(userRepository)

    @Test
    fun `authenticate returns true when user exists and password matches`() {
        val username = "user1"
        val password = "pass123"
        val user = UserAccount(username, password)
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        val result = userService.authenticate(username, password)

        assertTrue(result)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun `authenticate returns false when user does not exist`() {
        whenever(userRepository.findByUsername(any())).thenReturn(null)

        val result = userService.authenticate("anyUser", "anyPass")

        assertFalse(result)
        verify(userRepository).findByUsername("anyUser")
    }

    @Test
    fun `authenticate returns false when password does not match`() {
        val username = "user2"
        val user = UserAccount(username, "correctPass")
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        val result = userService.authenticate(username, "wrongPass")

        assertFalse(result)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun `exists returns true when userRepository existsById returns true`() {
        val username = "user3"
        whenever(userRepository.existsById(username)).thenReturn(true)

        val result = userService.exists(username)

        assertTrue(result)
        verify(userRepository).existsById(username)
    }

    @Test
    fun `exists returns false when userRepository existsById returns false`() {
        val username = "user4"
        whenever(userRepository.existsById(username)).thenReturn(false)

        val result = userService.exists(username)

        assertFalse(result)
        verify(userRepository).existsById(username)
    }

    @Test
    fun `register saves new user with given username and password`() {
        val username = "newUser"
        val password = "newPass"

        userService.register(username, password)

        val expectedUser = UserAccount(username, password)
        verify(userRepository).save(check {
            assertEquals(expectedUser.username, it.username)
            assertEquals(expectedUser.password, it.password)
        })
    }
}