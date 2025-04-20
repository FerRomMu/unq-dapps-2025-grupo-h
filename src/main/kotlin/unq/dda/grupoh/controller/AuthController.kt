package unq.dda.grupoh.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import unq.dda.grupoh.dto.LoginRequest
import unq.dda.grupoh.service.JwtService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val (username, password) = loginRequest

        // ---------- TODO VALIDACION HARDCODEADA
        if (username == "admin" && password == "admin") {
            val token = jwtService.generateToken(username)
            return ResponseEntity.ok(mapOf("token" to token))
        }
        // --------- TODO VALIDACION HARDCODEADA

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
    }
}
