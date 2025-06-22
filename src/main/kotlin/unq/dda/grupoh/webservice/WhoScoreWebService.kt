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
import unq.dda.grupoh.model.Feature
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.TeamFeatures
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

    fun findTeamFeatures(teamName: String): TeamFeatures {
        val teamUrl = findTeamUrl(teamName) // Reutilizamos la lógica para obtener la URL del equipo
        driver.get(teamUrl) // Navegamos a la página del equipo

        val wait = WebDriverWait(driver, Duration.ofSeconds(10))

        val strengthsList = mutableListOf<Feature>()
        val weaknessesList = mutableListOf<Feature>()
        val styleOfPlayList = mutableListOf<Feature>()

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.sws-content.character-card.singular")))

            // --- Extraer Fortalezas ---
            val strengthsContainer = driver.findElement(By.cssSelector("div.sws-content.character-card.singular div.strengths div.grid"))
            val strengthElements = strengthsContainer.findElements(By.cssSelector("div.character"))
            for (element in strengthElements) {
                val name = element.findElement(By.cssSelector("div.iconize")).text.trim()
                val value = element.findElement(By.cssSelector("span[class^='level']")).text.trim()
                strengthsList.add(Feature(name = name, value = value))
            }

            // --- Extraer Debilidades ---
            val weaknessesContainer = driver.findElement(By.cssSelector("div.sws-content.character-card.singular div.weaknesses div.grid"))
            val weaknessElements = weaknessesContainer.findElements(By.cssSelector("div.character"))
            for (element in weaknessElements) {
                val name = element.findElement(By.cssSelector("div.iconize")).text.trim()
                val value = element.findElement(By.cssSelector("span[class^='level']")).text.trim()
                weaknessesList.add(Feature(name = name, value = value))
            }

            // --- Extraer Estilo de Juego ---
            val styleOfPlayContainer = driver.findElement(By.cssSelector("div.sws-content.character-card.singular div.style ul"))
            val styleOfPlayElements = styleOfPlayContainer.findElements(By.cssSelector("li.character"))
            for (element in styleOfPlayElements) {
                // Para el estilo de juego, solo hay un nombre, no un "valor" Strong/Weak, así que el 'value' es nulo.
                val name = element.text.trim()
                styleOfPlayList.add(Feature(name = name))
            }

        } catch (e: Exception) {
            throw ResourceNotFoundException("No se pudieron cargar las características para el equipo '$teamName'. Error: ${e.message}")
        }

        return TeamFeatures(
            teamName = teamName,
            strengths = strengthsList,
            weaknesses = weaknessesList,
            styleOfPlay = styleOfPlayList
        )
    }
}