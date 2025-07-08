package edu.eci.arsw.code_arena.service

import edu.eci.arsw.code_arena.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.stereotype.Service

/**
 * Custom UserDetailsService for Spring Security authentication
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username).orElse(null)
            ?: userRepository.findByEmail(username).orElse(null)
            ?: throw UsernameNotFoundException("User not found with username or email: $username")

        if (!user.isActive) {
            throw UsernameNotFoundException("User account is deactivated")
        }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        return SpringUser.builder()
            .username(user.username)
            .password(user.passwordHash)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.isActive)
            .build()
    }
}
