package edu.eci.arsw.code_arena.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "games")
data class Game(
    @Id
    val id: String? = null,
    
    @Indexed(unique = true)
    val roomId: String,
    
    val hostId: String,
    
    val players: MutableList<GamePlayer> = mutableListOf(),
    
    val challenges: MutableList<GameChallenge> = mutableListOf(),
    
    val config: GameConfig,
    
    val settings: GameSettings? = null, // Alias para config
    
    val status: GameStatus = GameStatus.WAITING,
    
    val currentRound: Int = 0, // Current round number
    
    val roundNumber: Int = 0, // Alias for currentRound
    
    val timeLimit: Int = 60, // Time limit per round
    
    val results: GameResults? = null, // Game results when finished
    
    val winnerId: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val startedAt: LocalDateTime? = null,
    
    val finishedAt: LocalDateTime? = null,
    
    val currentChallengeIndex: Int = 0
)

data class GamePlayer(
    val userId: String,
    val username: String,
    var score: Int = 0,
    var totalAnswers: Int = 0,
    var correctAnswers: Int = 0,
    var currentStreak: Int = 0,
    var bestStreak: Int = 0,
    var hasAnswered: Boolean = false,
    var averageResponseTime: Double = 0.0,
    var responseTime: Double = 0.0, // For last answer
    var lastAnswerAt: LocalDateTime? = null,
    val answers: MutableList<PlayerAnswer> = mutableListOf(),
    val joinedAt: LocalDateTime = LocalDateTime.now(),
    var isConnected: Boolean = true
)

data class GameChallenge(
    val challengeId: String,
    val startedAt: LocalDateTime,
    val duration: Int, // En segundos
    val responses: MutableList<ChallengeResponse> = mutableListOf(),
    var isCompleted: Boolean = false
)

data class PlayerAnswer(
    val userId: String,
    val challengeId: String,
    val answer: String,
    val submittedAt: LocalDateTime,
    val isCorrect: Boolean,
    val timeToAnswer: Long, // En milisegundos
    val pointsEarned: Int,
    val score: Int = pointsEarned, // Alias para pointsEarned
    val responseTime: Double = timeToAnswer.toDouble() // Alias para timeToAnswer
)

data class ChallengeResponse(
    val playerId: String,
    val answer: String,
    val submittedAt: LocalDateTime,
    val isCorrect: Boolean,
    val timeToAnswer: Long,
    val pointsEarned: Int
)

data class GameConfig(
    val maxPlayers: Int = 4,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val gameMode: GameMode = GameMode.CLASSIC,
    val timePerChallenge: Int = 60, // En segundos
    val totalChallenges: Int = 5,
    val isPublic: Boolean = true
)

enum class GameStatus {
    WAITING,    // Esperando jugadores
    STARTING,   // Iniciando partida
    ACTIVE,     // Partida en curso
    IN_PROGRESS, // Alias for ACTIVE
    PAUSED,     // Partida pausada
    FINISHED,   // Partida terminada
    CANCELLED   // Partida cancelada
}

enum class GameMode {
    CLASSIC,    // Modo clásico
    BLITZ,      // Modo rápido
    PRACTICE,   // Modo práctica
    TOURNAMENT  // Modo torneo
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}
