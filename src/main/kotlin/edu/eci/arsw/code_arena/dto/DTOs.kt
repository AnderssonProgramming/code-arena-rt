package edu.eci.arsw.code_arena.dto

import edu.eci.arsw.code_arena.model.*
import jakarta.validation.constraints.*
import java.time.LocalDateTime

// Auth Request DTOs
data class RegisterRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    val username: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email or username is required")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class UpdateProfileRequest(
    val displayName: String? = null,
    val avatar: String? = null,
    val notifications: Boolean? = null,
    val soundEnabled: Boolean? = null,
    val theme: String? = null
)

// Auth Response DTOs
data class AuthResponse(
    val token: String,
    val user: UserDto,
    val message: String,
    val type: String = "Bearer"
)

data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val profile: UserProfile,
    val stats: UserStats,
    val settings: UserSettings,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime?
)

// Game Statistics Update
data class GameStatsUpdate(
    val won: Boolean,
    val score: Double,
    val playTime: Long // seconds
)

// API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: List<String>? = null
)

// Room DTOs
data class CreateRoomRequest(
    @field:NotBlank(message = "Room name is required")
    @field:Size(min = 3, max = 50, message = "Room name must be between 3 and 50 characters")
    val name: String,
    
    @field:Min(value = 2, message = "Minimum 2 players required")
    @field:Max(value = 8, message = "Maximum 8 players allowed")
    val maxPlayers: Int = 4,
    
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val isPrivate: Boolean = false,
    val roundsCount: Int = 5,
    val timePerRound: Int = 60
)

data class JoinRoomRequest(
    @field:NotBlank(message = "Room code is required")
    val roomCode: String
)

// WebSocket Message DTOs
data class WebSocketMessage(
    val type: String,
    val data: Any,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameEventMessage(
    val type: GameEventType,
    val gameId: String,
    val playerId: String? = null,
    val data: Any? = null
)

enum class GameEventType {
    PLAYER_JOINED,
    PLAYER_LEFT, 
    GAME_STARTED,
    ROUND_STARTED,
    ANSWER_SUBMITTED,
    ROUND_ENDED,
    GAME_ENDED,
    ERROR
}

// Additional DTOs for Game Controller
data class SubmitAnswerRequest(
    val gameId: String,
    val challengeId: String,
    val answer: String
)

data class AnswerResult(
    val isCorrect: Boolean,
    val score: Int,
    val responseTime: Double,
    val submittedAt: LocalDateTime,
    val correctAnswer: String? = null
)

data class ChallengeResponse(
    val id: String,
    val title: String,
    val description: String,
    val question: String,
    val options: List<String> = emptyList(),
    val type: ChallengeType,
    val difficulty: Difficulty,
    val timeLimit: Int,
    val points: Int,
    val hints: List<String> = emptyList()
)
