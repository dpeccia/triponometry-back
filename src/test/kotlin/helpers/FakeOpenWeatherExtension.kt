package helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import utn.triponometry.properties.TriponometryProperties
import java.io.File

class FakeOpenWeatherExtension(private val port: Int) : BeforeAllCallback, AfterEachCallback, AfterAllCallback {
    private lateinit var wireMockServer: WireMockServer

    override fun beforeAll(context: ExtensionContext?) {
        wireMockServer = WireMockServer(port)
        wireMockServer.start()
    }

    override fun afterEach(context: ExtensionContext?) {
        wireMockServer.resetAll()
    }

    override fun afterAll(context: ExtensionContext?) {
        wireMockServer.stop()
    }

    fun `stub scenario when get coordinates works successfully`() {
        val file = File("src/test/resources/coordinates_successful_response.json")
        stubResponse("/geo/.*".toRegex(), file.readText())
    }

    fun `stub scenario when get coordinates fails`() {
        val file = File("src/test/resources/coordinates_failure_response.json")
        stubResponse("/geo/.*".toRegex(), file.readText(), HttpStatus.UNAUTHORIZED.value())
    }

    fun `stub scenario when get weather works successfully`() {
        val file = File("src/test/resources/weather_successful_response.json")
        stubResponse("/data/.*".toRegex(), file.readText())
    }

    fun `stub scenario when get weather fails`() {
        val file = File("src/test/resources/weather_failure_response.json")
        stubResponse("/data/.*".toRegex(), file.readText(), HttpStatus.BAD_REQUEST.value())
    }

    private fun stubResponse(pathRegex: Regex, responseBody: String, responseStatus: Int = HttpStatus.OK.value()): StubMapping {
        val pathPattern = WireMock.urlPathMatching(pathRegex.toString())
        val requestPattern = WireMock.any(pathPattern)

        return wireMockServer.stubFor(
            requestPattern
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(responseStatus)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )
    }
}