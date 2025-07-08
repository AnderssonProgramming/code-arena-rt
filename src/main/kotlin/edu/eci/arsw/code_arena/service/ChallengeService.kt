package edu.eci.arsw.code_arena.service

import edu.eci.arsw.code_arena.model.*
import edu.eci.arsw.code_arena.repository.ChallengeRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for challenge management
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Service
@Transactional
class ChallengeService(
    private val challengeRepository: ChallengeRepository
) {

    /**
     * Get all challenges with pagination
     */
    fun getAllChallenges(page: Int = 0, size: Int = 20): List<Challenge> {
        val allChallenges = challengeRepository.findByIsActiveTrueOrderByCreatedAtDesc()
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, allChallenges.size)
        return if (startIndex < allChallenges.size) {
            allChallenges.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    /**
     * Get challenges by difficulty
     */
    fun getChallengesByDifficulty(difficulty: Difficulty): List<Challenge> {
        return challengeRepository.findByDifficultyAndIsActiveTrue(difficulty)
    }

    /**
     * Get challenges by type
     */
    fun getChallengesByType(type: ChallengeType): List<Challenge> {
        return challengeRepository.findByTypeAndIsActiveTrue(type)
    }

    /**
     * Get challenge by ID
     */
    fun getChallengeById(challengeId: String): Challenge {
        return challengeRepository.findById(challengeId).orElse(null)
            ?: throw IllegalArgumentException("Challenge not found")
    }

    /**
     * Create a new challenge (for admin/moderator use)
     */
    fun createChallenge(challenge: Challenge): Challenge {
        return challengeRepository.save(challenge)
    }

    /**
     * Update an existing challenge
     */
    fun updateChallenge(challengeId: String, updatedChallenge: Challenge): Challenge {
        val existingChallenge = getChallengeById(challengeId)
        
        val challenge = existingChallenge.copy(
            title = updatedChallenge.title,
            description = updatedChallenge.description,
            question = updatedChallenge.question,
            options = updatedChallenge.options,
            correctAnswer = updatedChallenge.correctAnswer,
            explanation = updatedChallenge.explanation,
            type = updatedChallenge.type,
            difficulty = updatedChallenge.difficulty,
            timeLimit = updatedChallenge.timeLimit,
            points = updatedChallenge.points,
            tags = updatedChallenge.tags,
            hints = updatedChallenge.hints
        )

        return challengeRepository.save(challenge)
    }

    /**
     * Delete a challenge (soft delete)
     */
    fun deleteChallenge(challengeId: String) {
        val challenge = getChallengeById(challengeId)
        val deactivatedChallenge = challenge.copy(isActive = false)
        challengeRepository.save(deactivatedChallenge)
    }

    /**
     * Search challenges by title or description
     */
    fun searchChallenges(query: String, limit: Int = 20): List<Challenge> {
        val results = challengeRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsActiveTrue(query, query)
        return results.take(limit)
    }

    /**
     * Get random challenge by difficulty
     */
    fun getRandomChallenge(difficulty: Difficulty): Challenge? {
        val challenges = getChallengesByDifficulty(difficulty)
        return challenges.randomOrNull()
    }

    /**
     * Get challenge statistics
     */
    fun getChallengeStatistics(challengeId: String): Map<String, Any> {
        val challenge = getChallengeById(challengeId)
        
        return mapOf(
            "id" to challenge.id!!,
            "title" to challenge.title,
            "difficulty" to challenge.difficulty,
            "type" to challenge.type,
            "timesPlayed" to challenge.stats.timesUsed,
            "successRate" to challenge.stats.successRate,
            "averageTime" to challenge.stats.averageTime,
            "createdAt" to challenge.createdAt
        )
    }

    /**
     * Initialize default challenges for the system
     */
    @Transactional
    fun initializeDefaultChallenges() {
        if (challengeRepository.count() == 0L) {
            val defaultChallenges = createDefaultChallenges()
            challengeRepository.saveAll(defaultChallenges)
        }
    }

    private fun createDefaultChallenges(): List<Challenge> {
        return listOf(
            // EASY Challenges
            Challenge(
                title = "Suma Básica",
                description = "Operación matemática simple",
                question = "¿Cuánto es 15 + 27?",
                type = ChallengeType.MULTIPLE_CHOICE,
                difficulty = Difficulty.EASY,
                options = listOf("40", "42", "44", "46"),
                correctAnswer = "42",
                explanation = "15 + 27 = 42",
                timeLimit = 30,
                points = 100,
                tags = listOf("matemáticas", "suma", "básico")
            ),
            
            Challenge(
                title = "Secuencia Lógica",
                description = "Encuentra el siguiente número en la secuencia",
                question = "2, 4, 6, 8, ?",
                type = ChallengeType.MULTIPLE_CHOICE,
                difficulty = Difficulty.EASY,
                options = listOf("9", "10", "11", "12"),
                correctAnswer = "10",
                explanation = "La secuencia son números pares consecutivos",
                timeLimit = 45,
                points = 120,
                tags = listOf("lógica", "secuencia", "números")
            ),

            // MEDIUM Challenges
            Challenge(
                title = "Factorial",
                description = "Calcula el factorial de un número",
                question = "¿Cuál es el factorial de 5?",
                type = ChallengeType.MULTIPLE_CHOICE,
                difficulty = Difficulty.MEDIUM,
                options = listOf("60", "120", "125", "150"),
                correctAnswer = "120",
                explanation = "5! = 5 × 4 × 3 × 2 × 1 = 120",
                timeLimit = 60,
                points = 200,
                tags = listOf("matemáticas", "factorial", "multiplicación")
            ),

            Challenge(
                title = "Ordenamiento de Arrays",
                description = "Complejidad de algoritmos de ordenamiento",
                question = "¿Cuál es la complejidad temporal promedio del algoritmo QuickSort?",
                type = ChallengeType.MULTIPLE_CHOICE,
                difficulty = Difficulty.MEDIUM,
                options = listOf("O(n)", "O(n log n)", "O(n²)", "O(log n)"),
                correctAnswer = "O(n log n)",
                explanation = "QuickSort tiene complejidad promedio O(n log n)",
                timeLimit = 90,
                points = 250,
                tags = listOf("algoritmos", "complejidad", "ordenamiento")
            ),

            // HARD Challenges
            Challenge(
                title = "Problema de las Torres de Hanoi",
                description = "Calculando movimientos mínimos",
                question = "¿Cuántos movimientos mínimos se necesitan para resolver Torres de Hanoi con 4 discos?",
                type = ChallengeType.OPEN_ANSWER,
                difficulty = Difficulty.HARD,
                correctAnswer = "15",
                explanation = "Fórmula: 2^n - 1, donde n=4. Entonces 2^4 - 1 = 15",
                timeLimit = 120,
                points = 400,
                tags = listOf("recursión", "torres-hanoi", "algoritmos"),
                hints = listOf("Usa la fórmula 2^n - 1", "n es el número de discos")
            ),

            Challenge(
                title = "Programación Dinámica",
                description = "Fibonacci optimizado",
                question = "¿Cuál es el 10mo número de Fibonacci? (comenzando desde F(0)=0, F(1)=1)",
                type = ChallengeType.OPEN_ANSWER,
                difficulty = Difficulty.HARD,
                correctAnswer = "55",
                explanation = "F(10) = 55. Secuencia: 0,1,1,2,3,5,8,13,21,34,55",
                timeLimit = 150,
                points = 450,
                tags = listOf("fibonacci", "programación-dinámica", "secuencias")
            )
        )
    }
}
