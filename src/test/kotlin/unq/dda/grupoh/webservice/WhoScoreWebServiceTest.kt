package unq.dda.grupoh.webservice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.openqa.selenium.*
import unq.dda.grupoh.exceptions.ResourceNotFoundException
import kotlin.test.assertEquals

class WhoScoreWebServiceTest {

    val whoScoreLink: String = "http://test.com/team"
    val cssTag: String = ".search-result table tbody tr:nth-of-type(2) td a"
    val elementTag: String = "#player-table-statistics-body tr"

    @Test
    fun shouldCorrectlyRetrievePlayersFromWebService() {
        val driver: WebDriver = mock()
        val teamLink: WebElement = mock()
        doReturn(whoScoreLink).whenever(teamLink).getAttribute("href")

        whenever(driver.findElement(By.cssSelector(cssTag)))
            .thenReturn(teamLink)

        val playerTableBody: WebElement = mock()
        whenever(driver.findElement(By.id("player-table-statistics-body")))
            .thenReturn(playerTableBody)

        val playerRow: WebElement = mock()

        whenever(driver.findElements(By.cssSelector(elementTag)))
            .thenReturn(listOf(playerRow))

        val playerNameElement: WebElement = mock()
        doReturn("Player Name").whenever(playerNameElement).text
        whenever(playerRow.findElement(By.cssSelector("td.grid-abs a.player-link span.iconize")))
            .thenReturn(playerNameElement)

        whenever(playerRow.findElements(By.cssSelector("td.grid-abs span.player-meta-data")))
            .thenReturn(emptyList())

        val heightElement: WebElement = mock { on { text } doReturn "180" }
        whenever(playerRow.findElement(By.xpath("./td[3]"))).thenReturn(heightElement)

        val weightElement: WebElement = mock { on { text } doReturn "75" }
        whenever(playerRow.findElement(By.xpath("./td[4]"))).thenReturn(weightElement)

        val ageElement: WebElement = mock { on { text } doReturn "20" }
        whenever(playerRow.findElement(By.xpath("./td[5]"))).thenReturn(ageElement)

        val minsPlayedElement: WebElement = mock { on { text } doReturn "1500" }
        whenever(playerRow.findElement(By.cssSelector("td.minsPlayed"))).thenReturn(minsPlayedElement)

        val goalElement: WebElement = mock { on { text } doReturn "5" }
        whenever(playerRow.findElement(By.cssSelector("td.goal"))).thenReturn(goalElement)

        val assistTotalElement: WebElement = mock { on { text } doReturn "3" }
        whenever(playerRow.findElement(By.cssSelector("td.assistTotal"))).thenReturn(assistTotalElement)

        val yellowCardElement: WebElement = mock { on { text } doReturn "2" }
        whenever(playerRow.findElement(By.cssSelector("td.yellowCard"))).thenReturn(yellowCardElement)

        val redCardElement: WebElement = mock { on { text } doReturn "0" }
        whenever(playerRow.findElement(By.cssSelector("td.redCard"))).thenReturn(redCardElement)

        val shotsPerGameElement: WebElement = mock { on { text } doReturn "1.5" }
        whenever(playerRow.findElement(By.cssSelector("td.shotsPerGame"))).thenReturn(shotsPerGameElement)

        val passSuccessElement: WebElement = mock { on { text } doReturn "85.0" }
        whenever(playerRow.findElement(By.cssSelector("td.passSuccess"))).thenReturn(passSuccessElement)

        val aerialWonPerGameElement: WebElement = mock { on { text } doReturn "2.1" }
        whenever(playerRow.findElement(By.cssSelector("td.aerialWonPerGame"))).thenReturn(aerialWonPerGameElement)

        val manOfTheMatchElement: WebElement = mock { on { text } doReturn "1" }
        whenever(playerRow.findElement(By.cssSelector("td.manOfTheMatch"))).thenReturn(manOfTheMatchElement)

        val ratingElement: WebElement = mock { on { text } doReturn "7.2" }
        whenever(playerRow.findElement(By.cssSelector("td.rating"))).thenReturn(ratingElement)

        val service = WhoScoreWebService(driver)

        val result = service.findPlayersByTeamName("Test")

        assertEquals(1, result.size)
        assertEquals("Player Name", result[0].name)
    }

        @Test
    fun shouldRiseExceptionIfTheIsNoPlayersTable() {
        val driver = mock<WebDriver>()
        val teamLink = mock<WebElement>()
        whenever(driver.get(any())).then { }
        whenever(driver.findElement(By.cssSelector(cssTag)))
            .thenReturn(teamLink)
        whenever(teamLink.getAttribute("href")).thenReturn(whoScoreLink)
        whenever(driver.findElements(By.cssSelector(elementTag)))
            .thenReturn(emptyList())

        val service = WhoScoreWebService(driver)

        assertThrows<ResourceNotFoundException> {
            service.findPlayersByTeamName("Test")
        }
    }

    @Test
    fun shouldRiseExceptionIfFailsToProccessPlayersRow() {
        val driver = mock<WebDriver>()
        val teamLink = mock<WebElement>()
        val row = mock<WebElement>()

        whenever(driver.get(any())).then { }
        whenever(driver.findElement(By.cssSelector(cssTag)))
            .thenReturn(teamLink)
        whenever(teamLink.getAttribute("href")).thenReturn(whoScoreLink)
        whenever(driver.findElements(By.cssSelector(elementTag)))
            .thenReturn(listOf(row))

        whenever(row.findElement(any())).thenThrow(RuntimeException("Error en fila"))

        val service = WhoScoreWebService(driver)

        assertThrows<ResourceNotFoundException> {
            service.findPlayersByTeamName("Test")
        }
    }
}