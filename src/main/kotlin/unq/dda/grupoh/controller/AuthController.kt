package unq.dda.grupoh.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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

    @Operation(
        summary = "Login de usuario",
        description = "Autentica al usuario con nombre de usuario y contraseña, devuelve token JWT si es correcto"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Autenticación exitosa, retorna token JWT",
                content = [Content(
                    mediaType = "application/json"
                )]
            ),
            ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val (username, password) = loginRequest

        if (userService.authenticate(username, password)) {
            val token = jwtService.generateToken(username)
            return ResponseEntity.ok(mapOf("token" to token))
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
    }

    @Operation(
        summary = "Registro de usuario",
        description = "Crea un nuevo usuario con nombre de usuario y contraseña"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            ApiResponse(responseCode = "409", description = "Nombre de usuario ya existente")
        ]
    )
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
