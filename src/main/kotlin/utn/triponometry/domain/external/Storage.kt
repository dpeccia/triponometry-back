package utn.triponometry.domain.external

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import utn.triponometry.domain.external.dtos.AgendaRequest
import utn.triponometry.helpers.AmazonException
import utn.triponometry.properties.TriponometryProperties
import java.util.*


@Component
class Storage(triponometryProperties: TriponometryProperties) {
    private val baseUrl = triponometryProperties.aws.url

    fun createAgenda(body: AgendaRequest): String {
        val id = UUID.randomUUID().toString()
        storeAgendaInAws(body.kml, id)
        return id
    }

    private fun storeAgendaInAws(body: String, id: String) {
        WebClient.create(baseUrl).put()
            .uri("/s3/${id}.xml")
            .bodyValue(body)
            .accept(MediaType.APPLICATION_XML)
            .retrieve()
            .onStatus(HttpStatus::isError) {
                val statusCode = it.statusCode()
                throw AmazonException("${statusCode.value()} - ${statusCode.reasonPhrase}")
            }
            .bodyToMono(String::class.java)
            .block()
    }

    fun getAgendaFromAws(id: String): String {
        val xml = WebClient.create(baseUrl).get()
            .uri("/s3/${id}")
            .retrieve()
            .onStatus(HttpStatus::isError) {
                val statusCode = it.statusCode()
                throw AmazonException("${statusCode.value()} - ${statusCode.reasonPhrase}")
            }
            .bodyToMono(String::class.java)
            .block()
        return xml!!
    }
}
