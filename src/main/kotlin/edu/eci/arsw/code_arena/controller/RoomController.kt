package edu.eci.arsw.code_arena.controller

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.model.Room
import edu.eci.arsw.code_arena.service.RoomService
import edu.eci.arsw.code_arena.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for room management
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = ["http://localhost:4200"])
class RoomController(
    private val roomService: RoomService,
    private val userService: UserService
) {

    /**
     * Create a new room
     */
    @PostMapping("/create")
    fun createRoom(
        authentication: Authentication,
        @Valid @RequestBody createRequest: CreateRoomRequest
    ): ResponseEntity<ApiResponse<Room>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            val room = roomService.createRoom(user.id, createRequest)
            
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(
                    success = true,
                    data = room,
                    message = "Room created successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to create room")
                )
            )
        }
    }

    /**
     * Join an existing room
     */
    @PostMapping("/join")
    fun joinRoom(
        authentication: Authentication,
        @Valid @RequestBody joinRequest: JoinRoomRequest
    ): ResponseEntity<ApiResponse<Room>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            val room = roomService.joinRoom(user.id, joinRequest.roomCode)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = room,
                    message = "Successfully joined room"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to join room")
                )
            )
        }
    }

    /**
     * Leave a room
     */
    @PostMapping("/{roomId}/leave")
    fun leaveRoom(
        authentication: Authentication,
        @PathVariable roomId: String
    ): ResponseEntity<ApiResponse<String>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            roomService.leaveRoom(user.id, roomId)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = "Left room successfully",
                    message = "Successfully left room"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to leave room")
                )
            )
        }
    }

    /**
     * Start game in room
     */
    @PostMapping("/{roomId}/start")
    fun startGame(
        authentication: Authentication,
        @PathVariable roomId: String
    ): ResponseEntity<ApiResponse<String>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            val game = roomService.startGame(user.id, roomId)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = game.id!!,
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
     * Get available public rooms
     */
    @GetMapping("/available")
    fun getAvailableRooms(): ResponseEntity<ApiResponse<List<Room>>> {
        return try {
            val rooms = roomService.getAvailableRooms()
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = rooms,
                    message = "Available rooms retrieved"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve rooms")
                )
            )
        }
    }

    /**
     * Get room by ID
     */
    @GetMapping("/{roomId}")
    fun getRoomById(@PathVariable roomId: String): ResponseEntity<ApiResponse<Room>> {
        return try {
            val room = roomService.getRoomById(roomId)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = room,
                    message = "Room retrieved successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Room not found")
                )
            )
        }
    }

    /**
     * Get room by code
     */
    @GetMapping("/code/{roomCode}")
    fun getRoomByCode(@PathVariable roomCode: String): ResponseEntity<ApiResponse<Room>> {
        return try {
            val room = roomService.getRoomByCode(roomCode)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = room,
                    message = "Room found"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Room not found")
                )
            )
        }
    }

    /**
     * Toggle player ready status
     */
    @PostMapping("/{roomId}/ready")
    fun toggleReady(
        authentication: Authentication,
        @PathVariable roomId: String
    ): ResponseEntity<ApiResponse<Room>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            val room = roomService.togglePlayerReady(user.id, roomId)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = room,
                    message = "Ready status updated"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to update ready status")
                )
            )
        }
    }

    /**
     * Get user's current rooms
     */
    @GetMapping("/my-rooms")
    fun getUserRooms(authentication: Authentication): ResponseEntity<ApiResponse<List<Room>>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            val rooms = roomService.getUserRooms(user.id)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = rooms,
                    message = "User rooms retrieved"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve user rooms")
                )
            )
        }
    }
}
