package edu.eci.arsw.code_arena.service

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.model.*
import edu.eci.arsw.code_arena.repository.RoomRepository
import edu.eci.arsw.code_arena.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Service for room management
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Service
@Transactional
class RoomService(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val gameService: GameService
) {

    /**
     * Create a new room
     */
    fun createRoom(userId: String, request: CreateRoomRequest): Room {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw IllegalArgumentException("User not found")

        val roomCode = generateRoomCode()
        
        val room = Room(
            roomCode = roomCode,
            name = request.name,
            hostUserId = userId,
            maxPlayers = request.maxPlayers,
            difficulty = request.difficulty,
            isPrivate = request.isPrivate,
            gameSettings = GameSettings(
                roundsCount = request.roundsCount,
                timePerRound = request.timePerRound
            )
        )

        // Add host as first player
        room.currentPlayers.add(
            RoomPlayer(
                userId = userId,
                username = user.username,
                isReady = true
            )
        )

        return roomRepository.save(room)
    }

    /**
     * Join an existing room
     */
    fun joinRoom(userId: String, roomCode: String): Room {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw IllegalArgumentException("User not found")

        val room = roomRepository.findByRoomCode(roomCode)
            ?: throw IllegalArgumentException("Room not found")

        if (room.status != RoomStatus.WAITING) {
            throw IllegalArgumentException("Room is not accepting new players")
        }

        if (room.isFull()) {
            throw IllegalArgumentException("Room is full")
        }

        if (room.hasPlayer(userId)) {
            throw IllegalArgumentException("Already in this room")
        }

        room.currentPlayers.add(
            RoomPlayer(
                userId = userId,
                username = user.username
            )
        )

        room.updatedAt = LocalDateTime.now()
        return roomRepository.save(room)
    }

    /**
     * Leave a room
     */
    fun leaveRoom(userId: String, roomId: String): Room? {
        val room = roomRepository.findById(roomId).orElse(null)
            ?: throw IllegalArgumentException("Room not found")

        room.currentPlayers.removeIf { it.userId == userId }

        // If host leaves, assign new host or close room
        if (room.hostUserId == userId) {
            if (room.currentPlayers.isNotEmpty()) {
                room.hostUserId = room.currentPlayers.first().userId
            } else {
                // Delete empty room
                roomRepository.delete(room)
                return null
            }
        }

        room.updatedAt = LocalDateTime.now()
        return roomRepository.save(room)
    }

    /**
     * Start a game in the room
     */
    fun startGame(userId: String, roomId: String): Game {
        val room = roomRepository.findById(roomId).orElse(null)
            ?: throw IllegalArgumentException("Room not found")

        if (!room.isHost(userId)) {
            throw IllegalArgumentException("Only room host can start the game")
        }

        if (!room.canStart()) {
            throw IllegalArgumentException("Cannot start game - not enough players or room not ready")
        }

        // Update room status
        val updatedRoom = room.copy(
            status = RoomStatus.IN_PROGRESS,
            startedAt = LocalDateTime.now()
        )
        roomRepository.save(updatedRoom)

        // Create and start game
        return gameService.createGame(room)
    }

    /**
     * Get all available public rooms
     */
    fun getAvailableRooms(): List<Room> {
        return roomRepository.findByIsPrivateFalseAndStatus(RoomStatus.WAITING)
    }

    /**
     * Get room by ID
     */
    fun getRoomById(roomId: String): Room {
        return roomRepository.findById(roomId).orElse(null)
            ?: throw IllegalArgumentException("Room not found")
    }

    /**
     * Get room by code
     */
    fun getRoomByCode(roomCode: String): Room {
        return roomRepository.findByRoomCode(roomCode)
            ?: throw IllegalArgumentException("Room not found")
    }

    /**
     * Toggle player ready status
     */
    fun togglePlayerReady(userId: String, roomId: String): Room {
        val room = roomRepository.findById(roomId).orElse(null)
            ?: throw IllegalArgumentException("Room not found")

        val player = room.currentPlayers.find { it.userId == userId }
            ?: throw IllegalArgumentException("Player not in room")

        val updatedPlayer = player.copy(isReady = !player.isReady)
        room.currentPlayers.removeIf { it.userId == userId }
        room.currentPlayers.add(updatedPlayer)

        room.updatedAt = LocalDateTime.now()
        return roomRepository.save(room)
    }

    /**
     * Get rooms where user is participating
     */
    fun getUserRooms(userId: String): List<Room> {
        return roomRepository.findByCurrentPlayersUserIdAndStatus(userId, RoomStatus.WAITING)
    }

    private fun generateRoomCode(): String {
        var code: String
        do {
            code = (1..6).map { Random.nextInt(0, 10) }.joinToString("")
        } while (roomRepository.existsByRoomCode(code))
        
        return code
    }
}
