package edu.eci.arsw.code_arena.controller

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.model.Challenge
import edu.eci.arsw.code_arena.model.Difficulty
import edu.eci.arsw.code_arena.model.ChallengeType
import edu.eci.arsw.code_arena.service.ChallengeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for challenge management
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = ["http://localhost:4200"])
class ChallengeController(
    private val challengeService: ChallengeService
) {

    /**
     * Get all challenges with pagination
     */
    @GetMapping
    fun getAllChallenges(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<List<ChallengeResponse>>> {
        return try {
            val challenges = challengeService.getAllChallenges(page, size)
            val challengeResponses = challenges.map { it.toChallengeResponse() }
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponses,
                    message = "Challenges retrieved successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve challenges")
                )
            )
        }
    }

    /**
     * Get challenges by difficulty
     */
    @GetMapping("/difficulty/{difficulty}")
    fun getChallengesByDifficulty(@PathVariable difficulty: Difficulty): ResponseEntity<ApiResponse<List<ChallengeResponse>>> {
        return try {
            val challenges = challengeService.getChallengesByDifficulty(difficulty)
            val challengeResponses = challenges.map { it.toChallengeResponse() }
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponses,
                    message = "Challenges retrieved by difficulty"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve challenges by difficulty")
                )
            )
        }
    }

    /**
     * Get challenges by type
     */
    @GetMapping("/type/{type}")
    fun getChallengesByType(@PathVariable type: ChallengeType): ResponseEntity<ApiResponse<List<ChallengeResponse>>> {
        return try {
            val challenges = challengeService.getChallengesByType(type)
            val challengeResponses = challenges.map { it.toChallengeResponse() }
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponses,
                    message = "Challenges retrieved by type"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve challenges by type")
                )
            )
        }
    }

    /**
     * Get challenge by ID
     */
    @GetMapping("/{challengeId}")
    fun getChallengeById(@PathVariable challengeId: String): ResponseEntity<ApiResponse<ChallengeResponse>> {
        return try {
            val challenge = challengeService.getChallengeById(challengeId)
            val challengeResponse = challenge.toChallengeResponse()
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponse,
                    message = "Challenge retrieved successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Challenge not found")
                )
            )
        }
    }

    /**
     * Search challenges
     */
    @GetMapping("/search")
    fun searchChallenges(
        @RequestParam query: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<ApiResponse<List<ChallengeResponse>>> {
        return try {
            val challenges = challengeService.searchChallenges(query, limit)
            val challengeResponses = challenges.map { it.toChallengeResponse() }
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponses,
                    message = "Search results retrieved"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Search failed")
                )
            )
        }
    }

    /**
     * Get random challenge by difficulty
     */
    @GetMapping("/random/{difficulty}")
    fun getRandomChallenge(@PathVariable difficulty: Difficulty): ResponseEntity<ApiResponse<ChallengeResponse?>> {
        return try {
            val challenge = challengeService.getRandomChallenge(difficulty)
            val challengeResponse = challenge?.toChallengeResponse()
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponse,
                    message = if (challenge != null) "Random challenge retrieved" else "No challenges available"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to get random challenge")
                )
            )
        }
    }

    /**
     * Get challenge statistics
     */
    @GetMapping("/{challengeId}/stats")
    fun getChallengeStatistics(@PathVariable challengeId: String): ResponseEntity<ApiResponse<Map<String, Any>>> {
        return try {
            val stats = challengeService.getChallengeStatistics(challengeId)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = stats,
                    message = "Challenge statistics retrieved"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve statistics")
                )
            )
        }
    }

    /**
     * Initialize default challenges (for setup)
     */
    @PostMapping("/initialize")
    fun initializeDefaultChallenges(): ResponseEntity<ApiResponse<String>> {
        return try {
            challengeService.initializeDefaultChallenges()
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = "Default challenges initialized",
                    message = "Default challenges have been loaded"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to initialize challenges")
                )
            )
        }
    }
}

// Extension function to convert Challenge to ChallengeResponse (without answer)
private fun Challenge.toChallengeResponse(): ChallengeResponse {
    return ChallengeResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        question = this.question,
        type = this.type,
        difficulty = this.difficulty,
        options = this.options,
        timeLimit = this.timeLimit,
        points = this.points,
        hints = this.hints
    )
}
