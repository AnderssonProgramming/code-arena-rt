package edu.eci.arsw.code_arena.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    var jwt: JwtProperties = JwtProperties(),
    var cors: CorsProperties = CorsProperties()
)

data class JwtProperties(
    var secret: String = "defaultSecretKey",
    var expiration: Long = 86400000L // 24 horas en milisegundos
)

data class CorsProperties(
    var allowedOrigins: String = "http://localhost:4200"
)
