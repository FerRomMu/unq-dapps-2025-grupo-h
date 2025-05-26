package unq.dda.grupoh.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.service.TeamService

@RestController
@RequestMapping("/team")
class TeamController(
    private val teamService: TeamService
) {

    @Operation(
        summary = "Obtiene jugadores de un equipo",
        description = "Devuelve la lista de nombres de jugadores que pertenecen al equipo especificado"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista de jugadores", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = String::class, type = "array"))
        ])
    )
    @GetMapping("/players")
    fun getPlayersByTeam(
        @RequestParam("name") teamName: String
    ): List<String> = teamService.getPlayersByTeamName(teamName)

    @Operation(
        summary = "Obtiene partidos de un equipo",
        description = "Devuelve la lista de todos los partidos en los que ha participado el equipo especificado"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista de partidos", content = [
            Content(mediaType = "application/json", schema = Schema(implementation = Match::class, type = "array"))
        ])
    )
    @GetMapping("/matches")
    fun getMatchesByTeam(
        @RequestParam("name") teamName: String
    ): List<Match> = teamService.getMatchesByTeamName(teamName)

    @Operation(
        summary = "Obtiene próximos partidos de un equipo",
        description = "Devuelve la lista de los próximos partidos programados para el equipo especificado"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Lista de próximos partidos", content = [
                Content(mediaType = "application/json", schema = Schema(implementation = Match::class, type = "array"))
            ]
        )
    )
    @GetMapping("/next-matches")
    fun getNextMatchesByTeam(
        @RequestParam("name") teamName: String
    ): List<Match> = teamService.getNextMatchesByTeamName(teamName)

}
