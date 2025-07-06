package edu.eci.arsw.code_arena.model

/**
 * Additional enums and data classes for the Code Arena application
 */

// Alias for ChallengeDifficulty to match Difficulty
typealias ChallengeDifficulty = Difficulty

enum class ScoringMode {
    STANDARD,       // Puntos por respuesta correcta + bonus por velocidad
    TIME_BASED,     // Más puntos por respuestas más rápidas
    STREAK_BONUS,   // Bonus por racha de respuestas correctas
    ELIMINATION     // Elimina jugadores con respuestas incorrectas
}

data class GameSettings(
    val roundCount: Int = 5,
    val timePerRound: Int = 60,
    val scoringMode: ScoringMode = ScoringMode.STANDARD,
    val allowHints: Boolean = true,
    val difficulty: Difficulty = Difficulty.MEDIUM
)

data class GameResults(
    val playerResults: List<PlayerResult>,
    val winner: String?,
    val totalRounds: Int,
    val gameStats: GameStatsResults
)

data class PlayerResult(
    val userId: String,
    val username: String,
    val score: Int,
    val correctAnswers: Int,
    val totalAnswers: Int,
    val averageResponseTime: Double,
    val rank: Int
)

data class GameStatsResults(
    val averageScore: Double,
    val fastestAnswer: Double,
    val hardestQuestion: String?
)

data class ChallengeGameStats(
    val challengeId: String,
    val title: String,
    val correctAnswers: Int,
    val totalAnswers: Int,
    val averageTime: Double,
    val successRate: Double
)
