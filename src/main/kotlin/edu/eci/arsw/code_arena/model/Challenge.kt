package edu.eci.arsw.code_arena.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "challenges")
data class Challenge(
    @Id
    val id: String? = null,
    
    val title: String,
    
    val description: String,
    
    @Indexed
    val type: ChallengeType,
    
    @Indexed
    val difficulty: Difficulty,
    
    val question: String,
    
    val options: List<String> = emptyList(), // Para preguntas de múltiple opción
    
    val correctAnswer: String,
    
    val explanation: String? = null,
    
    val timeLimit: Int = 60, // En segundos
    
    val baseScore: Int = 100,
    
    val points: Int = 100, // Alias para baseScore
    
    val hints: List<String> = emptyList(), // Pistas para el challenge
    
    val tags: List<String> = emptyList(),
    
    val createdBy: String? = null,
    
    val stats: ChallengeStats = ChallengeStats(),
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val isActive: Boolean = true,
    
    val timesPlayed: Int = 0 // Número de veces que se ha jugado este challenge
)

data class ChallengeStats(
    val timesUsed: Int = 0,
    val averageTime: Double = 0.0, // En segundos
    val successRate: Double = 0.0, // Porcentaje
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0
)

enum class ChallengeType {
    LOGIC,          // Rompecabezas de lógica
    MATH,           // Problemas matemáticos
    PATTERN,        // Reconocimiento de patrones
    CODE,           // Pseudocódigo/algoritmos
    TRIVIA,         // Preguntas de trivia técnica
    SEQUENCE,       // Secuencias numéricas/lógicas
    MULTIPLE_CHOICE, // Preguntas de múltiple opción
    OPEN_ANSWER     // Preguntas de respuesta abierta
}
