package unq.dda.grupoh.config

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException // Importar
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import unq.dda.grupoh.exceptions.ResourceNotFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [ResourceNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val errorDetails = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to HttpStatus.NOT_FOUND.value(),
            "error" to "Not Found",
            "message" to ex.message,
            "path" to request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [NoHandlerFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException, request: WebRequest): ResponseEntity<Any> {
        val errorDetails = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to HttpStatus.NOT_FOUND.value(),
            "error" to "Not Found",
            "message" to "No se encontró el endpoint: ${ex.requestURL}",
            "path" to request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(value = [IllegalArgumentException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<Any> {
        val errorDetails = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to ex.message,
            "path" to request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    // Maneja específicamente las AuthenticationException y sus subclases (401)
    @ExceptionHandler(value = [AuthenticationException::class])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(ex: AuthenticationException, request: WebRequest): ResponseEntity<Any> {
        val message = when (ex) {
            is SignatureException -> "Token inválido: Firma JWT no válida."
            is ExpiredJwtException -> "Token expirado: El JWT ha caducado."
            is BadCredentialsException -> "Credenciales inválidas."
            else -> "Error de autenticación: Acceso no autorizado."
        }
        val errorDetails = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to "Unauthorized",
            "message" to message,
            "path" to request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED)
    }

    // Manejo de cualquier otra excepción no capturada (500 Internal Server Error)
    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        println("Error interno del servidor: ${ex.message}")
        ex.printStackTrace()

        val errorDetails = mapOf(
            "timestamp" to System.currentTimeMillis(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Internal Server Error",
            "message" to "Ha ocurrido un error inesperado. Por favor, intente de nuevo más tarde.",
            "path" to request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}