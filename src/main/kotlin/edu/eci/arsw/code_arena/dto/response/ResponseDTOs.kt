package edu.eci.arsw.code_arena.dto.response

import edu.eci.arsw.code_arena.model.*
import java.time.LocalDateTime

// Auth DTOs
data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val user: UserResponse,
    val expiresIn: Long
)

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val profile: UserProfile,
    val stats: UserStats,
    val settings: UserSettings,
    val createdAt: LocalDateTime,
    val lastLoginAt: LocalDateTime?
)

// Room DTOs
data class RoomResponse(
    val id: String,
    val roomCode: String,
    val name: String,
    val hostId: String,
    val players: List<RoomPlayerResponse>,
    val config: RoomConfig,
    val status: RoomStatus,
    val createdAt: LocalDateTime,
    val gameId: String?
)

data class RoomPlayerResponse(
    val userId: String,
    val username: String,
    val joinedAt: LocalDateTime,
    val isReady: Boolean,
    val isConnected: Boolean
)

data class PublicRoomResponse(
    val id: String,
    val roomCode: String,
    val name: String,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val difficulty: Difficulty,
    val gameMode: GameMode,
    val status: RoomStatus,
    val requiresPassword: Boolean
)

// Game DTOs
data class GameResponse(
    val id: String,
    val roomId: String,
    val hostId: String,
    val players: List<GamePlayerResponse>,
    val config: GameConfig,
    val status: GameStatus,
    val currentChallengeIndex: Int,
    val createdAt: LocalDateTime,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val winnerId: String?
)

data class GamePlayerResponse(
    val userId: String,
    val username: String,
    val score: Int,
    val isConnected: Boolean,
    val joinedAt: LocalDateTime
)

data class ChallengeResponse(
    val id: String,
    val title: String,
    val description: String,
    val type: ChallengeType,
    val difficulty: Difficulty,
    val question: String,
    val options: List<String>,
    val timeLimit: Int,
    val baseScore: Int,
    val tags: List<String>
    // No incluimos correctAnswer ni explanation para seguridad
)

data class AnswerResultResponse(
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val timeToAnswer: Long,
    val correctAnswer: String?, // Solo se envía después de que termina el challenge
    val explanation: String?
)

data class GameStatsResponse(
    val playerId: String,
    val username: String,
    val score: Int,
    val correctAnswers: Int,
    val totalAnswers: Int,
    val averageTime: Double,
    val position: Int
)

// Leaderboard DTOs
data class LeaderboardResponse(
    val players: List<LeaderboardPlayer>,
    val updatedAt: LocalDateTime
)

data class LeaderboardPlayer(
    val userId: String,
    val username: String,
    val totalScore: Int,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val winRate: Double,
    val averageScore: Double,
    val level: Int,
    val position: Int
)

// Error DTOs
data class ErrorResponse(
    val message: String,
    val error: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val path: String? = null
)

data class ValidationErrorResponse(
    val message: String,
    val errors: Map<String, String>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

// Success DTOs
data class MessageResponse(
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
