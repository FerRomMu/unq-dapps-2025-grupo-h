package unq.dda.grupoh.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import unq.dda.grupoh.dto.LoginRequest
import unq.dda.grupoh.service.JwtService
import unq.dda.grupoh.service.UserService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtService: JwtService,
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val (username, password) = loginRequest

        if (userService.authenticate(username, password)) {
            val token = jwtService.generateToken(username)
            return ResponseEntity.ok(mapOf("token" to token))
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
    }

    @PostMapping("/register")
    fun register(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val (username, password) = loginRequest

        if (userService.exists(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken")
        }

        userService.register(username, password)
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
    }
}
