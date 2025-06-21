package unq.dda.grupoh.webservice

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.TeamPerformance
import unq.dda.grupoh.model.TournamentPerformance
import java.time.Duration

@Service
class WhoScoreWebService (
    private val driver: WebDriver = run { // Default value for 'driver'
        val options = ChromeOptions()
        options.addArguments("--headless=new")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")
        options.addArguments("window-size=1920,1080")
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        ChromeDriver(options)
    }
    ) {

    private fun extractPlayerFromRow(row: WebElement, teamName: String): Player {
        val nameElement = row.findElement(By.cssSelector("td.grid-abs a.player-link span.iconize"))
        val playerName = nameElement.text.trim()

        val playerDataSpans = row.findElements(By.cssSelector("td.grid-abs span.player-meta-data"))
        val age = playerDataSpans.getOrNull(0)?.text?.toIntOrNull()
        val detailedPosition = playerDataSpans.getOrNull(1)?.text?.trim()

        val heightCm = row.findElement(By.xpath("./td[3]")).text.toIntOrNull() // CM
        val weightKg = row.findElement(By.xpath("./td[4]")).text.toIntOrNull() // KG
        val matchesPlayed = row.findElement(By.xpath("./td[5]")).text.toIntOrNull() // Jgdos
        val minutesPlayed = row.findElement(By.cssSelector("td.minsPlayed")).text.toIntOrNull() // Mins
        val goals = row.findElement(By.cssSelector("td.goal")).text.toIntOrNull() // Goles
        val assists = row.findElement(By.cssSelector("td.assistTotal")).text.toIntOrNull() // Asist
        val yellowCards = row.findElement(By.cssSelector("td.yellowCard")).text.toIntOrNull() // Amar
        val redCards = row.findElement(By.cssSelector("td.redCard")).text.toIntOrNull() // Roja
        val shotsPerGame = row.findElement(By.cssSelector("td.shotsPerGame")).text.toDoubleOrNull() // TpP
        val passSuccessPercentage = row.findElement(By.cssSelector("td.passSuccess")).text.toDoubleOrNull() // AP%
        val aerialsWonPerGame = row.findElement(By.cssSelector("td.aerialWonPerGame")).text.toDoubleOrNull() // Aéreos
        val manOfTheMatch = row.findElement(By.cssSelector("td.manOfTheMatch")).text.toIntOrNull() // JdelP
        val rating = row.findElement(By.cssSelector("td.rating")).text.toDoubleOrNull() // Rating

        return Player(
            name = playerName,
            teamName = teamName,
            age = age,
            position = detailedPosition,
            heightCm = heightCm,
            weightKg = weightKg,
            matchesPlayed = matchesPlayed,
            minutesPlayed = minutesPlayed,
            goals = goals,
            assists = assists,
            yellowCards = yellowCards,
            redCards = redCards,
            shotsPerGame = shotsPerGame,
            passSuccessPercentage = passSuccessPercentage,
            aerialsWonPerGame = aerialsWonPerGame,
            manOfTheMatch = manOfTheMatch,
            rating = rating
        )
    }

    private fun extractPerformanceFromRow(row: WebElement): TournamentPerformance {
        val cells = row.findElements(By.tagName("td"))

        if (cells.size < 9) {
            throw IllegalArgumentException("La fila no tiene suficientes celdas para extraer el rendimiento del torneo.")
        }

        val tournament = cells[0].text.trim()
        val apps = cells[1].text.toIntOrNull()
        val goals = cells[2].text.toIntOrNull()
        val shotsPerGame = cells[3].text.toDoubleOrNull()

        var yellowCards: Int? = null
        var redCards: Int? = null
        try {
            val disciplineSpan = cells[4].findElement(By.cssSelector("span.yellow-card-box"))
            yellowCards = disciplineSpan.text.toIntOrNull()
            val redCardSpan = cells[4].findElement(By.cssSelector("span.red-card-box"))
            redCards = redCardSpan.text.toIntOrNull()
        } catch (e: org.openqa.selenium.NoSuchElementException) {
        }

        val possessionPercentage = cells[5].text.toDoubleOrNull()
        val passSuccessPercentage = cells[6].text.toDoubleOrNull()
        val aerialsWonPerGame = cells[7].text.toDoubleOrNull()
        val rating = cells[8].text.toDoubleOrNull()

        return TournamentPerformance(
            tournament = tournament,
            apps = apps,
            goals = goals,
            shotsPerGame = shotsPerGame,
            yellowCards = yellowCards,
            redCards = redCards,
            possessionPercentage = possessionPercentage,
            passSuccessPercentage = passSuccessPercentage,
            aerialsWonPerGame = aerialsWonPerGame,
            rating = rating
        )
    }

    private fun findTeamUrl(teamName: String): String {
        val searchUrl = "https://es.whoscored.com/search/?t=${teamName.replace(" ", "+")}"
        driver.get(searchUrl)

        val wait = WebDriverWait(driver, Duration.ofSeconds(5))

        val teamLink: WebElement? = try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".search-result table tbody tr:nth-of-type(2) td a")))
            driver.findElement(By.cssSelector(".search-result table tbody tr:nth-of-type(2) td a"))
        } catch (e: Exception) {
            throw ResourceNotFoundException("No se encontró el equipo '$teamName' o su URL en los resultados de búsqueda.")
        }

        val fullTeamUrl = teamLink?.getAttribute("href")

        if (fullTeamUrl.isNullOrEmpty()) {
            throw ResourceNotFoundException("No se encontró el equipo '$teamName' o su URL en los resultados de búsqueda.")
        }

        return if (fullTeamUrl.startsWith("http")) {
            fullTeamUrl
        } else {
            "https://es.whoscored.com$fullTeamUrl"
        }
    }

    fun findPlayersByTeamName(name: String): List<Player> {
        val teamUrl = findTeamUrl(name)
        driver.get(teamUrl)

        val players = mutableListOf<Player>()
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")))

            val playerRows = driver.findElements(By.cssSelector("#player-table-statistics-body tr"))

            if (playerRows.isEmpty()) {
                throw ResourceNotFoundException("No se encontraron jugadores en la tabla.")
            }

            for (row in playerRows) {
                try {
                    val player = extractPlayerFromRow(row, name)
                    players.add(player)
                } catch (e: Exception) {
                    throw ResourceNotFoundException("Error al procesar la fila de un jugador: ${e.message}. Fila: ${row.text.substring(0, Math.min(row.text.length, 100))}...")
                }
            }
        } catch (e: Exception) {
            throw ResourceNotFoundException("No se pudo cargar la tabla de jugadores para el equipo. Error: ${e.message}")
        }

        return players
    }

    fun findTeamPerformanceByName(teamName: String): TeamPerformance {
        val teamUrl = findTeamUrl(teamName)
        driver.get(teamUrl)

        val wait = WebDriverWait(driver, Duration.ofSeconds(10))

        val tournamentPerformances = mutableListOf<TournamentPerformance>()
        var meanPerformance: TournamentPerformance? = null

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("top-team-stats-summary-content")))

            val tableBody = driver.findElement(By.id("top-team-stats-summary-content"))
            val rows = tableBody.findElements(By.tagName("tr"))

            if (rows.isEmpty()) {
                throw ResourceNotFoundException("No se encontraron filas de rendimiento en la tabla de resumen del equipo.")
            }

            for (row in rows) {
                try {
                    val performance = extractPerformanceFromRow(row)
                    if (performance.tournament == "Total / Average") {
                        meanPerformance = performance
                    } else {
                        tournamentPerformances.add(performance)
                    }
                } catch (e: Exception) {
                    throw ResourceNotFoundException("Error al procesar la fila de rendimiento del equipo: ${e.message}. Fila: ${row.text.substring(0,
                        row.text.length.coerceAtMost(100)
                    )}...")
                }
            }

            if (meanPerformance == null) {
                throw ResourceNotFoundException("No se encontró la fila de 'Total / Average' para el rendimiento del equipo '$teamName'.")
            }

            return TeamPerformance(
                teamName = teamName,
                tournamentPerformances = tournamentPerformances,
                meanPerformance = meanPerformance
            )
        } catch (e: Exception) {
            throw ResourceNotFoundException("No se pudo cargar la información de rendimiento para el equipo '$teamName'. Error: ${e.message}")
        }
    }
}