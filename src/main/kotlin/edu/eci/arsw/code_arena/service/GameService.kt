package edu.eci.arsw.code_arena.service

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.model.*
import edu.eci.arsw.code_arena.repository.GameRepository
import edu.eci.arsw.code_arena.repository.ChallengeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Service for game management and game logic
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Service
@Transactional
class GameService(
    private val gameRepository: GameRepository,
    private val challengeRepository: ChallengeRepository
) {

    /**
     * Create a new game from a room
     */
    fun createGame(room: Room): Game {
        val challenges = selectChallengesForGame(room.gameSettings ?: GameSettings(), room.difficulty)
        
        val gamePlayers = room.currentPlayers.map { roomPlayer ->
            GamePlayer(
                userId = roomPlayer.userId,
                username = roomPlayer.username,
                joinedAt = roomPlayer.joinedAt
            )
        }.toMutableList()

        val gameChallenges = challenges.mapIndexed { index, challenge ->
            GameChallenge(
                challengeId = challenge.id!!,
                startedAt = LocalDateTime.now(),
                duration = room.gameSettings?.timePerRound ?: 60
            )
        }.toMutableList()

        val game = Game(
            roomId = room.id!!,
            hostId = room.hostId,
            config = GameConfig(
                maxPlayers = room.maxPlayers,
                difficulty = room.difficulty,
                timePerChallenge = room.gameSettings?.timePerRound ?: 60,
                totalChallenges = room.gameSettings?.roundCount ?: 5
            ),
            players = gamePlayers,
            challenges = gameChallenges,
            settings = room.gameSettings,
            status = GameStatus.STARTING
        )

        return gameRepository.save(game)
    }

    /**
     * Start the first round of a game
     */
    fun startGame(gameId: String): Game {
        val game = gameRepository.findById(gameId).orElse(null)
            ?: throw IllegalArgumentException("Game not found")

        if (game.status != GameStatus.STARTING && game.status != GameStatus.WAITING) {
            throw IllegalArgumentException("Game cannot be started")
        }

        var updatedGame = game.copy(
            status = GameStatus.IN_PROGRESS,
            startedAt = LocalDateTime.now(),
            currentRound = 1
        )

        // Start first challenge
        if (updatedGame.challenges.isNotEmpty()) {
            val firstChallenge = updatedGame.challenges[0].copy(startedAt = LocalDateTime.now())
            updatedGame.challenges[0] = firstChallenge
        }

        return gameRepository.save(updatedGame)
    }

    /**
     * Submit an answer for the current round
     */
    fun submitAnswer(gameId: String, userId: String, answer: String): PlayerAnswer {
        val game = gameRepository.findById(gameId).orElse(null)
            ?: throw IllegalArgumentException("Game not found")

        if (game.status != GameStatus.IN_PROGRESS) {
            throw IllegalArgumentException("Game is not in progress")
        }

        val currentChallenge = game.challenges.getOrNull(game.currentRound - 1)
            ?: throw IllegalArgumentException("No current challenge")

        val challenge = challengeRepository.findById(currentChallenge.challengeId).orElse(null)
            ?: throw IllegalArgumentException("Challenge not found")

        // Check if player already answered
        if (currentChallenge.responses.any { it.playerId == userId }) {
            throw IllegalArgumentException("Player already answered this round")
        }

        val isCorrect = checkAnswer(challenge, answer)
        val responseTime = calculateResponseTime(currentChallenge.startedAt)
        val score = calculateScore(challenge, isCorrect, responseTime, game.settings?.scoringMode ?: ScoringMode.STANDARD)

        val playerAnswer = PlayerAnswer(
            userId = userId,
            challengeId = challenge.id!!,
            answer = answer,
            submittedAt = LocalDateTime.now(),
            isCorrect = isCorrect,
            timeToAnswer = responseTime.toLong(),
            pointsEarned = score
        )

        // Add response to challenge
        val challengeResponse = edu.eci.arsw.code_arena.model.ChallengeResponse(
            playerId = userId,
            answer = answer,
            submittedAt = LocalDateTime.now(),
            isCorrect = isCorrect,
            timeToAnswer = responseTime,
            pointsEarned = score
        )
        
        // Find the mutable challenge and add the response
        val challengeIndex = game.challenges.indexOfFirst { it == currentChallenge }
        if (challengeIndex >= 0) {
            val mutableChallenges = game.challenges.toMutableList()
            val updatedResponses = currentChallenge.responses.toMutableList()
            updatedResponses.add(challengeResponse)
            val updatedChallenge = currentChallenge.copy(responses = updatedResponses)
            mutableChallenges[challengeIndex] = updatedChallenge
            val updatedGame = game.copy(challenges = mutableChallenges)
            gameRepository.save(updatedGame)
        }

        // Update player stats
        updatePlayerStats(game, userId, playerAnswer)

        // Check if round is complete
        if (isRoundComplete(game, currentChallenge)) {
            completeRound(game, currentChallenge)
        }

        gameRepository.save(game)
        return playerAnswer
    }

    /**
     * Get game by ID
     */
    fun getGameById(gameId: String): Game {
        return gameRepository.findById(gameId).orElse(null)
            ?: throw IllegalArgumentException("Game not found")
    }

    /**
     * Get current challenge for a game
     */
    fun getCurrentChallenge(gameId: String): Challenge? {
        val game = getGameById(gameId)
        val currentGameChallenge = game.challenges.getOrNull(game.currentRound - 1)
            ?: return null

        return challengeRepository.findById(currentGameChallenge.challengeId).orElse(null)
    }

    /**
     * Force end game (for testing or emergency situations)
     */
    fun endGame(gameId: String): Game {
        val game = gameRepository.findById(gameId).orElse(null)
            ?: throw IllegalArgumentException("Game not found")

        val results = calculateFinalResults(game)
        
        val endedGame = game.copy(
            status = GameStatus.FINISHED,
            finishedAt = LocalDateTime.now(),
            results = results
        )

        return gameRepository.save(endedGame)
    }

    private fun selectChallengesForGame(settings: GameSettings, difficulty: ChallengeDifficulty): List<Challenge> {
        val availableChallenges = challengeRepository.findByDifficultyAndIsActiveTrue(difficulty)
        
        if (availableChallenges.size < settings.roundCount) {
            throw IllegalArgumentException("Not enough challenges available for this difficulty")
        }

        return availableChallenges.shuffled().take(settings.roundCount)
    }

    private fun checkAnswer(challenge: Challenge, answer: String): Boolean {
        return challenge.correctAnswer.equals(answer.trim(), ignoreCase = true)
    }

    private fun calculateResponseTime(startTime: LocalDateTime): Long {
        return java.time.Duration.between(startTime, LocalDateTime.now()).toMillis()
    }

    private fun calculateScore(
        challenge: Challenge, 
        isCorrect: Boolean, 
        responseTime: Long, 
        scoringMode: ScoringMode
    ): Int {
        if (!isCorrect) return 0

        val baseScore = challenge.baseScore
        
        return when (scoringMode) {
            ScoringMode.TIME_BASED -> {
                val timeBonus = maxOf(0, (challenge.timeLimit * 1000 - responseTime) / 1000).toInt()
                baseScore + timeBonus
            }
            ScoringMode.STANDARD -> baseScore
            ScoringMode.STREAK_BONUS -> baseScore // TODO: implement streak logic
            ScoringMode.ELIMINATION -> baseScore // TODO: implement elimination logic
        }
    }

    private fun updatePlayerStats(game: Game, userId: String, answer: PlayerAnswer) {
        val playerIndex = game.players.indexOfFirst { it.userId == userId }
        if (playerIndex >= 0) {
            val player = game.players[playerIndex]
            val updatedPlayer = player.copy(
                score = player.score + answer.pointsEarned,
                totalAnswers = player.totalAnswers + 1,
                correctAnswers = if (answer.isCorrect) player.correctAnswers + 1 else player.correctAnswers,
                currentStreak = if (answer.isCorrect) player.currentStreak + 1 else 0,
                bestStreak = if (answer.isCorrect) maxOf(player.bestStreak, player.currentStreak + 1) else player.bestStreak,
                lastAnswerAt = LocalDateTime.now(),
                hasAnswered = true,
                responseTime = calculateAverageResponseTime(player, answer.timeToAnswer.toDouble())
            )
            
            // Create a new list with the updated player
            val mutablePlayers = game.players.toMutableList()
            mutablePlayers[playerIndex] = updatedPlayer
            val updatedGame = game.copy(players = mutablePlayers)
            gameRepository.save(updatedGame)
        }
    }

    private fun calculateAverageResponseTime(player: GamePlayer, newResponseTime: Double): Double {
        return if (player.totalAnswers == 0) {
            newResponseTime
        } else {
            ((player.responseTime * player.totalAnswers) + newResponseTime) / (player.totalAnswers + 1)
        }
    }

    private fun isRoundComplete(game: Game, challenge: GameChallenge): Boolean {
        return challenge.responses.size >= game.players.size || 
               java.time.Duration.between(challenge.startedAt, LocalDateTime.now()).seconds >= challenge.duration
    }

    private fun completeRound(game: Game, challenge: GameChallenge) {
        val updatedChallenge = challenge.copy(isCompleted = true)
        val challengeIndex = game.challenges.indexOf(challenge)
        if (challengeIndex >= 0) {
            val mutableChallenges = game.challenges.toMutableList()
            mutableChallenges[challengeIndex] = updatedChallenge
            val updatedGame = game.copy(challenges = mutableChallenges)
            gameRepository.save(updatedGame)
        }
        
        // Reset hasAnswered for all players
        val mutablePlayers = game.players.toMutableList()
        game.players.forEachIndexed { index, player ->
            mutablePlayers[index] = player.copy(hasAnswered = false)
        }
        val gameWithResetPlayers = game.copy(players = mutablePlayers)
        gameRepository.save(gameWithResetPlayers)

        // Check if game is complete
        if (game.currentRound >= game.challenges.size) {
            // Game is finished - this should be handled by calling endGame()
        }
    }

    private fun calculateFinalResults(game: Game): GameResults {
        val sortedPlayers = game.players.sortedByDescending { it.score }
        val winner = sortedPlayers.firstOrNull()
        
        val playerResults = sortedPlayers.mapIndexed { index, player ->
            PlayerResult(
                userId = player.userId,
                username = player.username,
                score = player.score,
                correctAnswers = player.correctAnswers,
                totalAnswers = player.totalAnswers,
                averageResponseTime = player.responseTime,
                rank = index + 1
            )
        }

        val gameStats = GameStatsResults(
            averageScore = if (playerResults.isNotEmpty()) playerResults.map { it.score }.average() else 0.0,
            fastestAnswer = playerResults.mapNotNull { 
                if (it.averageResponseTime > 0) it.averageResponseTime else null 
            }.minOrNull() ?: 0.0,
            hardestQuestion = null // TODO: determine hardest question
        )

        return GameResults(
            playerResults = playerResults,
            winner = winner?.userId,
            totalRounds = game.challenges.size,
            gameStats = gameStats
        )
    }
}
