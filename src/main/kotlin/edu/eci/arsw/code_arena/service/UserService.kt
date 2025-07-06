package edu.eci.arsw.code_arena.service

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.model.User
import edu.eci.arsw.code_arena.repository.UserRepository
import edu.eci.arsw.code_arena.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Service for user management and authentication
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    /**
     * Register a new user
     */
    fun registerUser(registerRequest: RegisterRequest): AuthResponse {
        // Validate if user already exists
        if (userRepository.existsByEmail(registerRequest.email)) {
            throw IllegalArgumentException("Email already exists")
        }
        
        if (userRepository.existsByUsername(registerRequest.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        // Create new user
        val user = User(
            email = registerRequest.email,
            username = registerRequest.username,
            passwordHash = passwordEncoder.encode(registerRequest.password)
        )

        val savedUser = userRepository.save(user)
        val token = jwtTokenProvider.generateToken(savedUser.username)

        return AuthResponse(
            token = token,
            user = savedUser.toUserDto(),
            message = "User registered successfully"
        )
    }

    /**
     * Authenticate user login
     */
    fun loginUser(loginRequest: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(loginRequest.email)
            ?: userRepository.findByUsername(loginRequest.email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(loginRequest.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        if (!user.isActive) {
            throw IllegalArgumentException("Account is deactivated")
        }

        // Update last login
        val updatedUser = user.copy(lastLoginAt = LocalDateTime.now())
        userRepository.save(updatedUser)

        val token = jwtTokenProvider.generateToken(user.username)

        return AuthResponse(
            token = token,
            user = updatedUser.toUserDto(),
            message = "Login successful"
        )
    }

    /**
     * Get user by username
     */
    fun getUserByUsername(username: String): UserDto {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found")
        
        return user.toUserDto()
    }

    /**
     * Get user by ID
     */
    fun getUserById(userId: String): UserDto {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw IllegalArgumentException("User not found")
        
        return user.toUserDto()
    }

    /**
     * Update user profile
     */
    fun updateUserProfile(userId: String, updateRequest: UpdateProfileRequest): UserDto {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw IllegalArgumentException("User not found")

        val updatedUser = user.copy(
            profile = user.profile.copy(
                displayName = updateRequest.displayName ?: user.profile.displayName,
                avatar = updateRequest.avatar ?: user.profile.avatar
            ),
            settings = user.settings.copy(
                notifications = updateRequest.notifications ?: user.settings.notifications,
                soundEnabled = updateRequest.soundEnabled ?: user.settings.soundEnabled,
                theme = updateRequest.theme ?: user.settings.theme
            )
        )

        val savedUser = userRepository.save(updatedUser)
        return savedUser.toUserDto()
    }

    /**
     * Update user statistics after a game
     */
    fun updateUserStats(userId: String, gameResult: GameStatsUpdate) {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw IllegalArgumentException("User not found")

        val currentStats = user.stats
        val newStreak = if (gameResult.won) currentStats.currentStreak + 1 else 0

        val updatedStats = currentStats.copy(
            gamesPlayed = currentStats.gamesPlayed + 1,
            gamesWon = if (gameResult.won) currentStats.gamesWon + 1 else currentStats.gamesWon,
            averageScore = calculateNewAverage(currentStats.averageScore, currentStats.gamesPlayed, gameResult.score),
            totalPlayTime = currentStats.totalPlayTime + gameResult.playTime,
            currentStreak = newStreak,
            bestStreak = maxOf(currentStats.bestStreak, newStreak)
        )

        val updatedUser = user.copy(stats = updatedStats)
        userRepository.save(updatedUser)
    }

    /**
     * Get user rankings
     */
    fun getUserRankings(limit: Int = 10): List<UserDto> {
        return userRepository.findTopUsersByWinRate(limit)
            .map { it.toUserDto() }
    }

    /**
     * Search users by username
     */
    fun searchUsers(query: String, limit: Int = 10): List<UserDto> {
        return userRepository.findByUsernameContainingIgnoreCase(query, limit)
            .map { it.toUserDto() }
    }

    /**
     * Validate if username is available
     */
    fun isUsernameAvailable(username: String): Boolean {
        return !userRepository.existsByUsername(username)
    }

    /**
     * Validate if email is available
     */
    fun isEmailAvailable(email: String): Boolean {
        return !userRepository.existsByEmail(email)
    }

    private fun calculateNewAverage(currentAverage: Double, gamesPlayed: Int, newScore: Double): Double {
        return if (gamesPlayed == 0) {
            newScore
        } else {
            ((currentAverage * gamesPlayed) + newScore) / (gamesPlayed + 1)
        }
    }
}

// Extension function to convert User to UserDto
private fun User.toUserDto(): UserDto {
    return UserDto(
        id = this.id!!,
        username = this.username,
        email = this.email,
        profile = this.profile,
        stats = this.stats,
        settings = this.settings,
        createdAt = this.createdAt,
        lastLoginAt = this.lastLoginAt
    )
}
