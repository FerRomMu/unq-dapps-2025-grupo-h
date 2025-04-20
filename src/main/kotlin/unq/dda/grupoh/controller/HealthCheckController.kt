package unq.dda.grupoh.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/ping")
    fun ping() = "pong"

    @GetMapping("/ping-logged")
    fun pingLogged() = "pong logueado"
}
