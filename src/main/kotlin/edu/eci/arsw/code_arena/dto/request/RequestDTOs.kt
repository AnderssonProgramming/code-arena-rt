package edu.eci.arsw.code_arena.dto.request

import edu.eci.arsw.code_arena.model.Difficulty
import edu.eci.arsw.code_arena.model.GameMode
import jakarta.validation.constraints.*

// Auth DTOs
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
    @field:NotBlank(message = "Username or email is required")
    val usernameOrEmail: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
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
    
    val gameMode: GameMode = GameMode.CLASSIC,
    
    @field:Min(value = 10, message = "Minimum 10 seconds per challenge")
    @field:Max(value = 300, message = "Maximum 300 seconds per challenge")
    val timePerChallenge: Int = 60,
    
    @field:Min(value = 1, message = "Minimum 1 challenge")
    @field:Max(value = 20, message = "Maximum 20 challenges")
    val totalChallenges: Int = 5,
    
    val isPublic: Boolean = true,
    
    val password: String? = null
)

data class JoinRoomRequest(
    @field:NotBlank(message = "Room code is required")
    val roomCode: String,
    
    val password: String? = null
)

// Game DTOs
data class SubmitAnswerRequest(
    @field:NotBlank(message = "Answer is required")
    val answer: String,
    
    @field:NotBlank(message = "Challenge ID is required")
    val challengeId: String,
    
    val timeToAnswer: Long = 0L
)

// Profile DTOs
data class UpdateProfileRequest(
    @field:Size(max = 50, message = "Display name must not exceed 50 characters")
    val displayName: String?,
    
    val avatar: String?,
    
    val settings: UserSettingsRequest?
)

data class UserSettingsRequest(
    val notifications: Boolean?,
    val soundEnabled: Boolean?,
    val theme: String?
)

// Challenge DTOs
data class CreateChallengeRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,
    
    @field:NotBlank(message = "Description is required")
    val description: String,
    
    @field:NotBlank(message = "Question is required")
    val question: String,
    
    val options: List<String> = emptyList(),
    
    @field:NotBlank(message = "Correct answer is required")
    val correctAnswer: String,
    
    val explanation: String?,
    
    val difficulty: Difficulty,
    
    val timeLimit: Int = 60,
    
    val baseScore: Int = 100,
    
    val tags: List<String> = emptyList()
)
