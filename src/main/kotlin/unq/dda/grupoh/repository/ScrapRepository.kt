package unq.dda.grupoh.repository

import org.openqa.selenium.By
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Repository
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import unq.dda.grupoh.model.Team
import java.time.Duration


class ScrapRepository {
    /*private fun createDriver(): WebDriver {
        val options = ChromeOptions()
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
            )
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL)
        options.setBinary(System.getenv("CHROME_BIN") ?: "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe")

        return ChromeDriver(options)
    }

    private fun fetchPlayers(teamName: String): List<String> {
        val driver: WebDriver = createDriver()
        val baseUrl = "https://es.whoscored.com"

        driver.get("$baseUrl/regions/11/tournaments/68/seasons/10573/argentina-liga-profesional")
        val wait = WebDriverWait(driver, Duration.ofSeconds(2))

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("standings")))
        val teamsTable = driver.findElement(By.className("standings"))
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-link")))
        val links = teamsTable.findElements(By.className("team-link"))

        val targetLink = links
            .firstOrNull {
                it.text.equals(teamName, true) || it.text.contains(teamName, true)
            }?.getAttribute("href") ?: throw ResourceNotFoundException("Team $teamName not found.")

        driver.get(targetLink)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")))
        val playersTable = driver.findElement(By.id("player-table-statistics-body"))

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("iconize")))

        val teamPlayers = playersTable.findElements(By.className("iconize"))
            .mapNotNull { it.text.takeIf { txt -> txt.isNotBlank() } }

        if (teamPlayers.isEmpty()) throw ResourceNotFoundException("Players of $teamName not found")

        driver.quit()

        return teamPlayers
    }*/

    //fun findByName(teamName: String): Team = Team(null, teamName, fetchPlayers(teamName))
}
