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
        val challenges = selectChallengesForGame(room.gameSettings, room.difficulty)
        
        val gamePlayers = room.currentPlayers.map { roomPlayer ->
            GamePlayer(
                userId = roomPlayer.userId,
                username = roomPlayer.username,
                joinedAt = roomPlayer.joinedAt
            )
        }

        val gameChallenges = challenges.mapIndexed { index, challenge ->
            GameChallenge(
                challengeId = challenge.id!!,
                roundNumber = index + 1,
                timeLimit = room.gameSettings.timePerRound
            )
        }

        val game = Game(
            roomId = room.id!!,
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

        val updatedGame = game.copy(
            status = GameStatus.IN_PROGRESS,
            startedAt = LocalDateTime.now(),
            currentRound = 1
        )

        // Start first challenge
        if (updatedGame.challenges.isNotEmpty()) {
            updatedGame.challenges[0].startedAt = LocalDateTime.now()
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
        if (currentChallenge.answers.any { it.userId == userId }) {
            throw IllegalArgumentException("Player already answered this round")
        }

        val isCorrect = checkAnswer(challenge, answer)
        val responseTime = calculateResponseTime(currentChallenge.startedAt!!)
        val score = calculateScore(challenge, isCorrect, responseTime, game.settings.scoringMode)

        val playerAnswer = PlayerAnswer(
            userId = userId,
            answer = answer,
            isCorrect = isCorrect,
            responseTime = responseTime,
            score = score
        )

        // Add answer to challenge
        currentChallenge.answers.add(playerAnswer)

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
        
        if (availableChallenges.size < settings.roundsCount) {
            throw IllegalArgumentException("Not enough challenges available for this difficulty")
        }

        return availableChallenges.shuffled().take(settings.roundsCount)
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
            ScoringMode.ACCURACY_ONLY -> baseScore
            ScoringMode.STREAK_BONUS -> baseScore // Streak handling in updatePlayerStats
        }
    }

    private fun updatePlayerStats(game: Game, userId: String, answer: PlayerAnswer) {
        val playerIndex = game.players.indexOfFirst { it.userId == userId }
        if (playerIndex >= 0) {
            val player = game.players[playerIndex]
            val updatedPlayer = player.copy(
                score = player.score + answer.score,
                totalAnswers = player.totalAnswers + 1,
                correctAnswers = if (answer.isCorrect) player.correctAnswers + 1 else player.correctAnswers,
                currentStreak = if (answer.isCorrect) player.currentStreak + 1 else 0,
                bestStreak = if (answer.isCorrect) maxOf(player.bestStreak, player.currentStreak + 1) else player.bestStreak,
                lastAnswerAt = LocalDateTime.now(),
                hasAnswered = true,
                averageResponseTime = calculateAverageResponseTime(player, answer.responseTime)
            )
            
            // Replace player in list (need to modify the actual game object)
            (game.players as MutableList)[playerIndex] = updatedPlayer
        }
    }

    private fun calculateAverageResponseTime(player: GamePlayer, newResponseTime: Long): Double {
        return if (player.totalAnswers == 0) {
            newResponseTime.toDouble()
        } else {
            ((player.averageResponseTime * player.totalAnswers) + newResponseTime) / (player.totalAnswers + 1)
        }
    }

    private fun isRoundComplete(game: Game, challenge: GameChallenge): Boolean {
        return challenge.answers.size >= game.players.size || 
               challenge.startedAt?.let { 
                   java.time.Duration.between(it, LocalDateTime.now()).seconds >= challenge.timeLimit 
               } ?: false
    }

    private fun completeRound(game: Game, challenge: GameChallenge) {
        challenge.isCompleted = true
        
        // Reset hasAnswered for all players
        (game.players as MutableList).replaceAll { it.copy(hasAnswered = false) }

        // Check if game is complete
        if (game.currentRound >= game.challenges.size) {
            // Game is finished
            val results = calculateFinalResults(game)
            (game as Game).copy(
                status = GameStatus.FINISHED,
                finishedAt = LocalDateTime.now(),
                results = results
            )
        } else {
            // Move to next round
            val nextRound = game.currentRound + 1
            (game as Game).copy(currentRound = nextRound)
            
            // Start next challenge
            if (nextRound <= game.challenges.size) {
                game.challenges[nextRound - 1].startedAt = LocalDateTime.now()
            }
        }
    }

    private fun calculateFinalResults(game: Game): GameResults {
        val sortedPlayers = game.players.sortedByDescending { it.score }
        val winner = sortedPlayers.firstOrNull()
        
        val totalDuration = game.startedAt?.let { start ->
            java.time.Duration.between(start, LocalDateTime.now()).toMillis()
        } ?: 0L

        val averageResponseTime = game.players
            .filter { it.totalAnswers > 0 }
            .map { it.averageResponseTime }
            .average()

        val challengeStats = game.challenges.associate { gameChallenge ->
            gameChallenge.challengeId to ChallengeGameStats(
                challengeId = gameChallenge.challengeId,
                correctAnswers = gameChallenge.answers.count { it.isCorrect },
                totalAnswers = gameChallenge.answers.size,
                averageResponseTime = gameChallenge.answers.map { it.responseTime }.average(),
                fastestAnswer = gameChallenge.answers.minOfOrNull { it.responseTime } ?: 0L,
                slowestAnswer = gameChallenge.answers.maxOfOrNull { it.responseTime } ?: 0L
            )
        }

        return GameResults(
            winner = winner,
            rankings = sortedPlayers,
            totalDuration = totalDuration,
            averageResponseTime = averageResponseTime,
            challengeStatistics = challengeStats
        )
    }
}
