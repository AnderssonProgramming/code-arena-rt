package edu.eci.arsw.code_arena.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenUtil {

    @Value("\${app.jwt.secret:CodeArenaSecretKeyForJWTTokenGeneration2025}")
    private lateinit var secret: String

    @Value("\${app.jwt.expiration:86400000}")
    private var jwtExpiration: Long = 86400000L

    private fun getSigningKey(): Key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun getUsernameFromToken(token: String): String = getClaimFromToken(token, Claims::getSubject)

    fun getExpirationDateFromToken(token: String): Date = getClaimFromToken(token, Claims::getExpiration)

    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun isTokenExpired(token: String): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return createToken(claims, userDetails.username)
    }

    fun generateTokenWithClaims(userDetails: UserDetails, additionalClaims: Map<String, Any>): String {
        return createToken(additionalClaims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username: String = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }

    fun getExpirationTime(): Long = jwtExpiration
}
