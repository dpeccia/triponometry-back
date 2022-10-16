package utn.triponometry.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**").allowedOrigins("https://www.triponometry.org/")
            .allowedMethods("*").allowedHeaders("*")
            .exposedHeaders("*").allowCredentials(true)
    }
}