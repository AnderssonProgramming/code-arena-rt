package edu.eci.arsw.code_arena.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

/**
 * JWT Token Provider - Alias for JwtTokenUtil to maintain consistency
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Component
class JwtTokenProvider(
    private val jwtTokenUtil: JwtTokenUtil
) {
    
    fun generateToken(userDetails: UserDetails): String {
        return jwtTokenUtil.generateToken(userDetails)
    }
    
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        return jwtTokenUtil.validateToken(token, userDetails)
    }
    
    fun getUsernameFromToken(token: String): String {
        return jwtTokenUtil.getUsernameFromToken(token)
    }
    
    fun isTokenExpired(token: String): Boolean {
        return jwtTokenUtil.isTokenExpired(token)
    }
}
