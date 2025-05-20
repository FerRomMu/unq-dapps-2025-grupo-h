package unq.dda.grupoh.external

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import unq.dda.grupoh.dto.footballData.*
import unq.dda.grupoh.exceptions.ExternalErrorException
import unq.dda.grupoh.model.Match
import unq.dda.grupoh.model.Player
import unq.dda.grupoh.model.Team

class FootballDataServiceTest {

}