package utn.triponometry.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class AppConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val om = ObjectMapper()
        om.registerModule(ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
        om.registerModule(KotlinModule())
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        val timeModule = JavaTimeModule()
        val localDateTimeDeserializer = LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        timeModule.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)
        om.registerModule(timeModule)
        om.dateFormat = SimpleDateFormat("dd/MM/yyyy")
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        om.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        om.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        om.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true)
        return om
    }
}