package utn.triponometry.domain.external

import com.google.gson.Gson
import com.google.maps.model.GeocodingResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import utn.triponometry.domain.external.dtos.AgendaId
import utn.triponometry.domain.external.dtos.AgendaRequest
import utn.triponometry.helpers.AmazonException
import utn.triponometry.helpers.OpenWeatherException
import utn.triponometry.properties.TriponometryProperties
import java.net.http.HttpClient


@Component
class Storage(triponometryProperties: TriponometryProperties) {
    private val baseUrl = triponometryProperties.aws.url

    fun createAgenda(body: AgendaRequest): String{
        val result = storeAgendaInAws(body)
        return Gson().fromJson(result, AgendaId::class.java).id
    }

    private fun storeAgendaInAws(body: AgendaRequest) =
        WebClient.create(baseUrl).post()
            .uri("/agenda")
            .bodyValue(body)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError) {
                val statusCode = it.statusCode()
                throw AmazonException("${statusCode.value()} - ${statusCode.reasonPhrase}")
            }
            .bodyToMono(String::class.java)
            .block() ?: throw AmazonException("There was an error with AWS Server")


}