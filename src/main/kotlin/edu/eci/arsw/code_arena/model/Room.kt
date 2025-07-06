package edu.eci.arsw.code_arena.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "rooms")
data class Room(
    @Id
    val id: String? = null,
    
    @Indexed(unique = true)
    val roomCode: String,
    
    val name: String,
    
    val hostId: String,
    
    val hostUserId: String = hostId, // Alias for hostId
    
    val players: MutableList<RoomPlayer> = mutableListOf(),
    
    val currentPlayers: MutableList<RoomPlayer> = players, // Alias for players
    
    val config: RoomConfig,
    
    val gameSettings: GameSettings? = null, // Additional game settings
    
    val maxPlayers: Int = config.maxPlayers,
    
    val difficulty: Difficulty = config.difficulty,
    
    val isPrivate: Boolean = !config.isPublic,
    
    val status: RoomStatus = RoomStatus.WAITING,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    val startedAt: LocalDateTime? = null,
    
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(30),
    
    val gameId: String? = null
) {
    // Computed properties
    val isFull: Boolean
        get() = players.size >= config.maxPlayers
    
    fun hasPlayer(userId: String): Boolean = players.any { it.userId == userId }
    
    fun isHost(userId: String): Boolean = hostId == userId || hostUserId == userId
    
    fun canStart(): Boolean = players.size >= 2 && players.all { it.isReady }
}

data class RoomPlayer(
    val userId: String,
    val username: String,
    val joinedAt: LocalDateTime = LocalDateTime.now(),
    var isReady: Boolean = false,
    var isConnected: Boolean = true
)

data class RoomConfig(
    val maxPlayers: Int = 4,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val gameMode: GameMode = GameMode.CLASSIC,
    val timePerChallenge: Int = 60,
    val totalChallenges: Int = 5,
    val isPublic: Boolean = true,
    val requiresPassword: Boolean = false,
    val password: String? = null
)

enum class RoomStatus {
    WAITING,    // Esperando jugadores
    READY,      // Listos para comenzar
    STARTING,   // Iniciando juego
    IN_GAME,    // Juego en progreso
    IN_PROGRESS, // Alias for IN_GAME
    FINISHED,   // Juego terminado
    CLOSED      // Sala cerrada
}
