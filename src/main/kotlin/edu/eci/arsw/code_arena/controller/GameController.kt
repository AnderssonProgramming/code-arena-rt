package edu.eci.arsw.code_arena.controller

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.model.Game
import edu.eci.arsw.code_arena.model.Challenge
import edu.eci.arsw.code_arena.service.GameService
import edu.eci.arsw.code_arena.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for game management
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = ["http://localhost:4200"])
class GameController(
    private val gameService: GameService,
    private val userService: UserService
) {

    /**
     * Get game by ID
     */
    @GetMapping("/{gameId}")
    fun getGameById(@PathVariable gameId: String): ResponseEntity<ApiResponse<Game>> {
        return try {
            val game = gameService.getGameById(gameId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = game,
                    message = "Game retrieved successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Game not found")
                )
            )
        }
    }

    /**
     * Start a game
     */
    @PostMapping("/{gameId}/start")
    fun startGame(@PathVariable gameId: String): ResponseEntity<ApiResponse<Game>> {
        return try {
            val game = gameService.startGame(gameId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = game,
                    message = "Game started successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to start game")
                )
            )
        }
    }

    /**
     * Submit an answer
     */
    @PostMapping("/{gameId}/answer")
    fun submitAnswer(
        authentication: Authentication,
        @PathVariable gameId: String,
        @RequestBody answerRequest: SubmitAnswerRequest
    ): ResponseEntity<ApiResponse<edu.eci.arsw.code_arena.dto.AnswerResult>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            val playerAnswer = gameService.submitAnswer(gameId, user.id, answerRequest.answer)
            
            val result = AnswerResult(
                isCorrect = playerAnswer.isCorrect,
                score = playerAnswer.score,
                responseTime = playerAnswer.responseTime,
                submittedAt = playerAnswer.submittedAt
            )
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = result,
                    message = if (playerAnswer.isCorrect) "Correct answer!" else "Incorrect answer"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to submit answer")
                )
            )
        }
    }

    /**
     * Get current challenge for a game
     */
    @GetMapping("/{gameId}/current-challenge")
    fun getCurrentChallenge(@PathVariable gameId: String): ResponseEntity<ApiResponse<ChallengeResponse>> {
        return try {
            val challenge = gameService.getCurrentChallenge(gameId)
            
            if (challenge == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse(
                        success = false,
                        message = "No current challenge available"
                    )
                )
            }
            
            val challengeResponse = ChallengeResponse(
                id = challenge.id!!,
                title = challenge.title,
                description = challenge.description,
                question = challenge.question,
                type = challenge.type,
                difficulty = challenge.difficulty,
                options = challenge.options,
                timeLimit = challenge.timeLimit,
                points = challenge.points,
                hints = challenge.hints
            )
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = challengeResponse,
                    message = "Current challenge retrieved"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve current challenge")
                )
            )
        }
    }

    /**
     * End a game (for emergency situations)
     */
    @PostMapping("/{gameId}/end")
    fun endGame(@PathVariable gameId: String): ResponseEntity<ApiResponse<Game>> {
        return try {
            val game = gameService.endGame(gameId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = game,
                    message = "Game ended successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to end game")
                )
            )
        }
    }
}

// Additional DTOs for Game Controller
data class SubmitAnswerRequest(
    val answer: String
)

data class AnswerResult(
    val isCorrect: Boolean,
    val score: Int,
    val responseTime: Long,
    val submittedAt: java.time.LocalDateTime
)

data class ChallengeResponse(
    val id: String,
    val title: String,
    val description: String,
    val question: String,
    val type: edu.eci.arsw.code_arena.model.ChallengeType,
    val difficulty: edu.eci.arsw.code_arena.model.Difficulty,
    val options: List<String>,
    val timeLimit: Int,
    val points: Int,
    val hints: List<String>
)
