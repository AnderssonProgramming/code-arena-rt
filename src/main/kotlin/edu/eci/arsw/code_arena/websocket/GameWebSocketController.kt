package edu.eci.arsw.code_arena.websocket

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.service.RoomService
import edu.eci.arsw.code_arena.service.GameService
import edu.eci.arsw.code_arena.service.UserService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import java.security.Principal

/**
 * WebSocket controller for real-time game communication
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Controller
class GameWebSocketController(
    private val roomService: RoomService,
    private val gameService: GameService,
    private val userService: UserService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    /**
     * Handle player joining a room
     */
    @MessageMapping("/room.join")
    fun joinRoom(
        @Payload joinMessage: RoomJoinMessage,
        headerAccessor: SimpMessageHeaderAccessor,
        principal: Principal
    ) {
        try {
            val username = principal.name
            val user = userService.getUserByUsername(username)
            val room = roomService.joinRoom(user.id, joinMessage.roomCode)
            
            // Add username to session
            headerAccessor.sessionAttributes?.put("username", username)
            headerAccessor.sessionAttributes?.put("roomId", room.id)
            
            // Notify all players in room
            val updateMessage = RoomUpdateMessage(
                type = "PLAYER_JOINED",
                room = room,
                player = user.username,
                message = "${user.username} joined the room"
            )
            
            messagingTemplate.convertAndSend("/topic/room.${room.id}", updateMessage)
            
        } catch (e: Exception) {
            // Send error to specific user
            val errorMessage = ErrorMessage(
                type = "JOIN_ROOM_ERROR",
                message = e.message ?: "Failed to join room"
            )
            messagingTemplate.convertAndSendToUser(principal.name, "/queue/errors", errorMessage)
        }
    }

    /**
     * Handle player leaving a room
     */
    @MessageMapping("/room.leave")
    fun leaveRoom(
        @Payload leaveMessage: RoomLeaveMessage,
        headerAccessor: SimpMessageHeaderAccessor,
        principal: Principal
    ) {
        try {
            val username = principal.name
            val user = userService.getUserByUsername(username)
            val room = roomService.leaveRoom(user.id, leaveMessage.roomId)
            
            if (room != null) {
                // Notify remaining players
                val updateMessage = RoomUpdateMessage(
                    type = "PLAYER_LEFT",
                    room = room,
                    player = user.username,
                    message = "${user.username} left the room"
                )
                
                messagingTemplate.convertAndSend("/topic/room.${room.id}", updateMessage)
            }
            
            // Clear session
            headerAccessor.sessionAttributes?.remove("roomId")
            
        } catch (e: Exception) {
            val errorMessage = ErrorMessage(
                type = "LEAVE_ROOM_ERROR",
                message = e.message ?: "Failed to leave room"
            )
            messagingTemplate.convertAndSendToUser(principal.name, "/queue/errors", errorMessage)
        }
    }

    /**
     * Handle player ready status change
     */
    @MessageMapping("/room.ready")
    fun toggleReady(
        @Payload readyMessage: PlayerReadyMessage,
        principal: Principal
    ) {
        try {
            val username = principal.name
            val user = userService.getUserByUsername(username)
            val room = roomService.togglePlayerReady(user.id, readyMessage.roomId)
            
            val updateMessage = RoomUpdateMessage(
                type = "PLAYER_READY_CHANGED",
                room = room,
                player = user.username,
                message = "${user.username} is ${if (room.currentPlayers.find { it.userId == user.id }?.isReady == true) "ready" else "not ready"}"
            )
            
            messagingTemplate.convertAndSend("/topic/room.${room.id}", updateMessage)
            
        } catch (e: Exception) {
            val errorMessage = ErrorMessage(
                type = "READY_ERROR",
                message = e.message ?: "Failed to update ready status"
            )
            messagingTemplate.convertAndSendToUser(principal.name, "/queue/errors", errorMessage)
        }
    }

    /**
     * Handle game start
     */
    @MessageMapping("/game.start")
    fun startGame(
        @Payload startMessage: GameStartMessage,
        principal: Principal
    ) {
        try {
            val username = principal.name
            val user = userService.getUserByUsername(username)
            val game = roomService.startGame(user.id, startMessage.roomId)
            
            // Start the actual game
            val startedGame = gameService.startGame(game.id!!)
            val currentChallenge = gameService.getCurrentChallenge(game.id!!)
            
            val gameStartedMessage = GameStartedMessage(
                gameId = startedGame.id!!,
                challenge = currentChallenge,
                message = "Game started! Good luck!"
            )
            
            messagingTemplate.convertAndSend("/topic/game.${startedGame.id}", gameStartedMessage)
            
        } catch (e: Exception) {
            val errorMessage = ErrorMessage(
                type = "START_GAME_ERROR",
                message = e.message ?: "Failed to start game"
            )
            messagingTemplate.convertAndSendToUser(principal.name, "/queue/errors", errorMessage)
        }
    }

    /**
     * Handle answer submission
     */
    @MessageMapping("/game.answer")
    fun submitAnswer(
        @Payload answerMessage: GameAnswerMessage,
        principal: Principal
    ) {
        try {
            val username = principal.name
            val user = userService.getUserByUsername(username)
            val playerAnswer = gameService.submitAnswer(answerMessage.gameId, user.id, answerMessage.answer)
            
            // Send result to specific player
            val answerResult = AnswerResultMessage(
                isCorrect = playerAnswer.isCorrect,
                score = playerAnswer.score,
                responseTime = playerAnswer.responseTime.toLong(),
                message = if (playerAnswer.isCorrect) "Correct!" else "Incorrect!"
            )
            
            messagingTemplate.convertAndSendToUser(username, "/queue/answer-result", answerResult)
            
            // Notify all players about the submission (without revealing correctness)
            val gameUpdateMessage = GameUpdateMessage(
                type = "ANSWER_SUBMITTED",
                gameId = answerMessage.gameId,
                player = username,
                message = "${username} submitted an answer"
            )
            
            messagingTemplate.convertAndSend("/topic/game.${answerMessage.gameId}", gameUpdateMessage)
            
        } catch (e: Exception) {
            val errorMessage = ErrorMessage(
                type = "SUBMIT_ANSWER_ERROR",
                message = e.message ?: "Failed to submit answer"
            )
            messagingTemplate.convertAndSendToUser(principal.name, "/queue/errors", errorMessage)
        }
    }

    /**
     * Handle chat messages in room
     */
    @MessageMapping("/room.chat")
    @SendTo("/topic/room.{roomId}.chat")
    fun sendChatMessage(
        @Payload chatMessage: ChatMessage,
        principal: Principal
    ): ChatMessage {
        return chatMessage.copy(
            sender = principal.name,
            timestamp = System.currentTimeMillis()
        )
    }
}

// WebSocket Message DTOs
data class RoomJoinMessage(
    val roomCode: String
)

data class RoomLeaveMessage(
    val roomId: String
)

data class PlayerReadyMessage(
    val roomId: String
)

data class GameStartMessage(
    val roomId: String
)

data class GameAnswerMessage(
    val gameId: String,
    val answer: String
)

data class ChatMessage(
    val roomId: String,
    val message: String,
    val sender: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class RoomUpdateMessage(
    val type: String,
    val room: edu.eci.arsw.code_arena.model.Room,
    val player: String,
    val message: String
)

data class GameStartedMessage(
    val gameId: String,
    val challenge: edu.eci.arsw.code_arena.model.Challenge?,
    val message: String
)

data class GameUpdateMessage(
    val type: String,
    val gameId: String,
    val player: String,
    val message: String
)

data class AnswerResultMessage(
    val isCorrect: Boolean,
    val score: Int,
    val responseTime: Long,
    val message: String
)

data class ErrorMessage(
    val type: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
