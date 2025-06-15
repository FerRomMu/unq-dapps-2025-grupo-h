package unq.dda.grupoh.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import unq.dda.grupoh.controller.AuthController
import unq.dda.grupoh.dto.LoginRequest
import unq.dda.grupoh.service.JwtService
import unq.dda.grupoh.service.UserService

class AuthControllerTest {
    private val jwtService = mock(JwtService::class.java)
    private val userService = mock(UserService::class.java)
    private val controller = AuthController(jwtService, userService)

    @Test
    fun `login should return token when credentials are valid`() {
        val username = "fercho"
        val test_pswd = "secreto"
        val token = "fake-jwt-token"

        `when`(userService.authenticate(username, test_pswd)).thenReturn(true)
        `when`(jwtService.generateToken(username)).thenReturn(token)

        val response = controller.login(LoginRequest(username, test_pswd))

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(mapOf("token" to token), response.body)
        verify(userService).authenticate(username, test_pswd)
        verify(jwtService).generateToken(username)
    }

    @Test
    fun `login should return 401 when credentials are invalid`() {
        val username = "fercho"
        val test_pswd = "wrong"

        `when`(userService.authenticate(username, test_pswd)).thenReturn(false)

        val response = controller.login(LoginRequest(username, test_pswd))

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Invalid credentials", response.body)
        verify(userService).authenticate(username, test_pswd)
        verify(jwtService, never()).generateToken(anyString())
    }

    @Test
    fun `register should return 409 when user exists`() {
        val username = "fercho"
        val test_pswd = "pass123"

        `when`(userService.exists(username)).thenReturn(true)

        val response = controller.register(LoginRequest(username, test_pswd))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Username already taken", response.body)
        verify(userService).exists(username)
        verify(userService, never()).register(anyString(), anyString())
    }

    @Test
    fun `register should return 201 when user is new`() {
        val username = "nuevo"
        val test_pswd = "123456"

        `when`(userService.exists(username)).thenReturn(false)

        val response = controller.register(LoginRequest(username, test_pswd))

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("User registered successfully", response.body)
        verify(userService).exists(username)
        verify(userService).register(username, test_pswd)
    }
}