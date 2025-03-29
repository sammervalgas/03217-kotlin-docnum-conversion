package br.com.devbean.convertershub.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {

    @Bean
    fun objectMapper() : ObjectMapper {
        val om = ObjectMapper()
        om.registerModules(JavaTimeModule())
        return om
    }

}