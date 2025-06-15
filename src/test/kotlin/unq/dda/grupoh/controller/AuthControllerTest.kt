package unq.dda.grupoh.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import unq.dda.grupoh.dto.LoginRequest
import unq.dda.grupoh.service.JwtService
import unq.dda.grupoh.service.UserService

class AuthControllerTest {
    private val jwtService = mock(JwtService::class.java)
    private val userService = mock(UserService::class.java)
    private val controller = AuthController(jwtService, userService)

    @Test
    fun loginShouldReturnTokenWhenCredentialsAreValid() {
        val username = "fercho"
        val testPswd = "secreto"
        val token = "fake-jwt-token"

        `when`(userService.authenticate(username, testPswd)).thenReturn(true)
        `when`(jwtService.generateToken(username)).thenReturn(token)

        val response = controller.login(LoginRequest(username, testPswd))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mapOf("token" to token), response.body)
        verify(userService).authenticate(username, testPswd)
        verify(jwtService).generateToken(username)
    }

    @Test
    fun loginShouldReturn401WhenCredentialsAreInvalid() {
        val username = "fercho"
        val testPswd = "wrong"

        `when`(userService.authenticate(username, testPswd)).thenReturn(false)

        val response = controller.login(LoginRequest(username, testPswd))

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Invalid credentials", response.body)
        verify(userService).authenticate(username, testPswd)
        verify(jwtService, never()).generateToken(anyString())
    }

    @Test
    fun registerShouldReturn409whenUserExists() {
        val username = "fercho"
        val testPswd = "pass123"

        `when`(userService.exists(username)).thenReturn(true)

        val response = controller.register(LoginRequest(username, testPswd))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Username already taken", response.body)
        verify(userService).exists(username)
        verify(userService, never()).register(anyString(), anyString())
    }

    @Test
    fun registerShouldReturn201WhenUserIsNew() {
        val username = "nuevo"
        val testPswd = "123456"

        `when`(userService.exists(username)).thenReturn(false)

        val response = controller.register(LoginRequest(username, testPswd))

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("User registered successfully", response.body)
        verify(userService).exists(username)
        verify(userService).register(username, testPswd)
    }
}