package unq.dda.grupoh.webservice

import org.junit.jupiter.api.Assertions.assertTrue
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
    val playerTableBodyId: String = "player-table-statistics-body"
    val playerNameCss: String = "td.grid-abs a.player-link span.iconize"
    val playerMetaDataCss: String = "td.grid-abs span.player-meta-data"
    val heightXpath: String = "./td[3]"
    val weightXpath: String = "./td[4]"
    val ageXpath: String = "./td[5]"
    val minsPlayedCss: String = "td.minsPlayed"
    val goalCss: String = "td.goal"
    val assistTotalCss: String = "td.assistTotal"
    val yellowCardCss: String = "td.yellowCard"
    val redCardCss: String = "td.redCard"
    val shotsPerGameCss: String = "td.shotsPerGame"
    val passSuccessCss: String = "td.passSuccess"
    val aerialWonPerGameCss: String = "td.aerialWonPerGame"
    val manOfTheMatchCss: String = "td.manOfTheMatch"
    val ratingCss: String = "td.rating"
    val mainCharacterCardCss: String = "div.sws-content.character-card.singular"
    val strengthsContainerCss: String = "div.sws-content.character-card.singular div.strengths div.grid"
    val weaknessesContainerCss: String = "div.sws-content.character-card.singular div.weaknesses div.grid"
    val styleOfPlayContainerCss: String = "div.sws-content.character-card.singular div.style ul"
    val characterCss: String = "div.character"
    val iconizeCss: String = "div.iconize"
    val levelSpanCss: String = "span[class^='level']"
    val listItemCharacterCss: String = "li.character"

    @Test
    fun shouldCorrectlyRetrievePlayersFromWebService() {
        val driver: WebDriver = mock()
        val teamLink: WebElement = mock()
        doReturn(whoScoreLink).whenever(teamLink).getAttribute("href")

        whenever(driver.findElement(By.cssSelector(cssTag)))
            .thenReturn(teamLink)

        val playerTableBody: WebElement = mock()
        whenever(driver.findElement(By.id(playerTableBodyId)))
            .thenReturn(playerTableBody)

        val playerRow: WebElement = mock()

        whenever(driver.findElements(By.cssSelector(elementTag)))
            .thenReturn(listOf(playerRow))

        val playerNameElement: WebElement = mock()
        doReturn("Player Name").whenever(playerNameElement).text
        whenever(playerRow.findElement(By.cssSelector(playerNameCss)))
            .thenReturn(playerNameElement)

        whenever(playerRow.findElements(By.cssSelector(playerMetaDataCss)))
            .thenReturn(emptyList())

        val heightElement: WebElement = mock { on { text } doReturn "180" }
        whenever(playerRow.findElement(By.xpath(heightXpath))).thenReturn(heightElement)

        val weightElement: WebElement = mock { on { text } doReturn "75" }
        whenever(playerRow.findElement(By.xpath(weightXpath))).thenReturn(weightElement)

        val ageElement: WebElement = mock { on { text } doReturn "20" }
        whenever(playerRow.findElement(By.xpath(ageXpath))).thenReturn(ageElement)

        val minsPlayedElement: WebElement = mock { on { text } doReturn "1500" }
        whenever(playerRow.findElement(By.cssSelector(minsPlayedCss))).thenReturn(minsPlayedElement)

        val goalElement: WebElement = mock { on { text } doReturn "5" }
        whenever(playerRow.findElement(By.cssSelector(goalCss))).thenReturn(goalElement)

        val assistTotalElement: WebElement = mock { on { text } doReturn "3" }
        whenever(playerRow.findElement(By.cssSelector(assistTotalCss))).thenReturn(assistTotalElement)

        val yellowCardElement: WebElement = mock { on { text } doReturn "2" }
        whenever(playerRow.findElement(By.cssSelector(yellowCardCss))).thenReturn(yellowCardElement)

        val redCardElement: WebElement = mock { on { text } doReturn "0" }
        whenever(playerRow.findElement(By.cssSelector(redCardCss))).thenReturn(redCardElement)

        val shotsPerGameElement: WebElement = mock { on { text } doReturn "1.5" }
        whenever(playerRow.findElement(By.cssSelector(shotsPerGameCss))).thenReturn(shotsPerGameElement)

        val passSuccessElement: WebElement = mock { on { text } doReturn "85.0" }
        whenever(playerRow.findElement(By.cssSelector(passSuccessCss))).thenReturn(passSuccessElement)

        val aerialWonPerGameElement: WebElement = mock { on { text } doReturn "2.1" }
        whenever(playerRow.findElement(By.cssSelector(aerialWonPerGameCss))).thenReturn(aerialWonPerGameElement)

        val manOfTheMatchElement: WebElement = mock { on { text } doReturn "1" }
        whenever(playerRow.findElement(By.cssSelector(manOfTheMatchCss))).thenReturn(manOfTheMatchElement)

        val ratingElement: WebElement = mock { on { text } doReturn "7.2" }
        whenever(playerRow.findElement(By.cssSelector(ratingCss))).thenReturn(ratingElement)

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

    @Test
    fun shouldCorrectlyRetrieveTeamFeaturesFromWebService() {
        val driver: WebDriver = mock()
        val teamLink: WebElement = mock()

        doReturn(whoScoreLink).whenever(teamLink).getAttribute("href")
        whenever(driver.findElement(By.cssSelector(cssTag))).thenReturn(teamLink)
        whenever(driver.get(any())).thenAnswer { /* Do nothing */ }

        val mainCharacterCard = mock<WebElement>()
        whenever(driver.findElement(By.cssSelector(mainCharacterCardCss))).thenReturn(mainCharacterCard)

        val strengthsContainer: WebElement = mock()
        whenever(driver.findElement(By.cssSelector(strengthsContainerCss))).thenReturn(strengthsContainer)

        val strength1 = mock<WebElement>()
        val strength1Name = mock<WebElement>()
        doReturn("Strong Passing").whenever(strength1Name).text
        val strength1Value = mock<WebElement>()
        doReturn("Strong").whenever(strength1Value).text
        whenever(strength1.findElement(By.cssSelector(iconizeCss))).thenReturn(strength1Name)
        whenever(strength1.findElement(By.cssSelector(levelSpanCss))).thenReturn(strength1Value)

        val strength2 = mock<WebElement>()
        val strength2Name = mock<WebElement>()
        doReturn("Aerial Duels").whenever(strength2Name).text
        val strength2Value = mock<WebElement>()
        doReturn("Very Strong").whenever(strength2Value).text
        whenever(strength2.findElement(By.cssSelector(iconizeCss))).thenReturn(strength2Name)
        whenever(strength2.findElement(By.cssSelector(levelSpanCss))).thenReturn(strength2Value)

        whenever(strengthsContainer.findElements(By.cssSelector(characterCss))).thenReturn(listOf(strength1, strength2))

        val weaknessesContainer: WebElement = mock()
        whenever(driver.findElement(By.cssSelector(weaknessesContainerCss))).thenReturn(weaknessesContainer)

        val weakness1 = mock<WebElement>()
        val weakness1Name = mock<WebElement>()
        doReturn("Defending Set-Pieces").whenever(weakness1Name).text
        val weakness1Value = mock<WebElement>()
        doReturn("Weak").whenever(weakness1Value).text
        whenever(weakness1.findElement(By.cssSelector(iconizeCss))).thenReturn(weakness1Name)
        whenever(weakness1.findElement(By.cssSelector(levelSpanCss))).thenReturn(weakness1Value)

        whenever(weaknessesContainer.findElements(By.cssSelector(characterCss))).thenReturn(listOf(weakness1))

        val styleOfPlayContainer: WebElement = mock()
        whenever(driver.findElement(By.cssSelector(styleOfPlayContainerCss))).thenReturn(styleOfPlayContainer)

        val style1 = mock<WebElement>()
        doReturn("Possession Football").whenever(style1).text
        val style2 = mock<WebElement>()
        doReturn("Short Passes").whenever(style2).text

        whenever(styleOfPlayContainer.findElements(By.cssSelector(listItemCharacterCss))).thenReturn(listOf(style1, style2))

        val service = WhoScoreWebService(driver)

        val result = service.findTeamFeatures("Test Team")

        assertEquals("Test Team", result.teamName)
        assertEquals(2, result.strengths.size)
        assertEquals("Strong Passing", result.strengths[0].name)
        assertEquals("Strong", result.strengths[0].value)
        assertEquals("Aerial Duels", result.strengths[1].name)
        assertEquals("Very Strong", result.strengths[1].value)

        assertEquals(1, result.weaknesses.size)
        assertEquals("Defending Set-Pieces", result.weaknesses[0].name)
        assertEquals("Weak", result.weaknesses[0].value)

        assertEquals(2, result.styleOfPlay.size)
        assertEquals("Possession Football", result.styleOfPlay[0].name)
        assertEquals(null, result.styleOfPlay[0].value)
        assertEquals("Short Passes", result.styleOfPlay[1].name)
        assertEquals(null, result.styleOfPlay[1].value)

        verify(driver).get("https://es.whoscored.com/search/?t=Test+Team")
        verify(driver).get(whoScoreLink)
    }

    @Test
    fun shouldRiseExceptionIfMainFeaturesContainerIsNotFound() {
        val driver: WebDriver = mock()
        val teamLink: WebElement = mock()

        doReturn(whoScoreLink).whenever(teamLink).getAttribute("href")
        whenever(driver.findElement(By.cssSelector(cssTag))).thenReturn(teamLink)
        whenever(driver.get(any())).thenAnswer { /* Do nothing */ }

        whenever(driver.findElement(By.cssSelector(mainCharacterCardCss))).thenThrow(org.openqa.selenium.NoSuchElementException("Main container not found"))

        val service = WhoScoreWebService(driver)

        val exception = assertThrows<ResourceNotFoundException> {
            service.findTeamFeatures("Test Team")
        }
        assertTrue(exception.message!!.contains("No se pudieron cargar las características para el equipo 'Test Team'."))
    }

    @Test
    fun shouldHandleEmptyStrengthsWeaknessesAndStyleOfPlayGracefully() {
        val driver: WebDriver = mock()
        val teamLink: WebElement = mock()

        doReturn(whoScoreLink).whenever(teamLink).getAttribute("href")
        whenever(driver.findElement(By.cssSelector(cssTag))).thenReturn(teamLink)
        whenever(driver.get(any())).thenAnswer { /* Do nothing */ }

        val mainCharacterCard = mock<WebElement>()
        whenever(driver.findElement(By.cssSelector(mainCharacterCardCss))).thenReturn(mainCharacterCard)

        val strengthsContainer: WebElement = mock()
        whenever(driver.findElement(By.cssSelector(strengthsContainerCss))).thenReturn(strengthsContainer)
        whenever(strengthsContainer.findElements(By.cssSelector(characterCss))).thenReturn(emptyList())

        val weaknessesContainer: WebElement = mock()
        whenever(driver.findElement(By.cssSelector(weaknessesContainerCss))).thenReturn(weaknessesContainer)
        whenever(weaknessesContainer.findElements(By.cssSelector(characterCss))).thenReturn(emptyList())

        val styleOfPlayContainer: WebElement = mock()
        whenever(driver.findElement(By.cssSelector(styleOfPlayContainerCss))).thenReturn(styleOfPlayContainer)
        whenever(styleOfPlayContainer.findElements(By.cssSelector(listItemCharacterCss))).thenReturn(emptyList())

        val service = WhoScoreWebService(driver)

        val result = service.findTeamFeatures("Test Team")

        assertEquals("Test Team", result.teamName)
        assertTrue(result.strengths.isEmpty())
        assertTrue(result.weaknesses.isEmpty())
        assertTrue(result.styleOfPlay.isEmpty())
    }

    @Test
    fun shouldRiseExceptionIfTeamUrlIsNotFoundForFeatures() {
        val driver = mock<WebDriver>()
        val teamName = "NonExistentTeam"

        whenever(driver.findElement(By.cssSelector(cssTag))).thenThrow(org.openqa.selenium.NoSuchElementException("No team link found"))
        whenever(driver.get(any())).thenAnswer { /* Do nothing */ }

        val service = WhoScoreWebService(driver)

        val exception = assertThrows<ResourceNotFoundException> {
            service.findTeamFeatures(teamName)
        }
        assertTrue(exception.message!!.contains("No se encontró el equipo '$teamName' o su URL en los resultados de búsqueda."))
    }
}