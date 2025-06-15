package unq.dda.grupoh.service

import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class JwtServiceTest {

    private val jwtService = JwtService()

    @Test
    fun generateTokenReturnsNonEmptyToken() {
        val token = jwtService.generateToken("user123")
        assertNotNull(token)
        assertTrue(token.isNotBlank())
    }

    @Test
    fun validateTokenReturnsUsernameWhenTokenValid() {
        val username = "user123"
        val token = jwtService.generateToken(username)
        val result = jwtService.validateToken(token)
        assertEquals(username, result)
    }

    @Test
    fun validateTokenReturnsNullWhenTokenInvalid() {
        val invalidToken = "invalid.token.value"
        val result = jwtService.validateToken(invalidToken)
        assertNull(result)
    }

    @Test
    fun validateTokenReturnsNullWhenTokenExpired() {
        val expiredJwtService = object : JwtService("wellknown.passwordkey-for-this-test12345678") {
            override fun generateToken(username: String): String {
                return io.jsonwebtoken.Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2))
                    .setExpiration(Date(System.currentTimeMillis() - 1000 * 60))
                    .signWith(Keys.hmacShaKeyFor("wellknown.passwordkey-for-this-test12345678".toByteArray()))
                    .compact()
            }
        }
        val token = expiredJwtService.generateToken("user123")
        val result = expiredJwtService.validateToken(token)
        assertNull(result)
    }
}