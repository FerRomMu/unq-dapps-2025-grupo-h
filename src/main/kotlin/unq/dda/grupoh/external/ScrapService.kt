package unq.dda.grupoh.external


class ScrapService {
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
