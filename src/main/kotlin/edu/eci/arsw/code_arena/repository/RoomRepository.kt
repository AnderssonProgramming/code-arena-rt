package edu.eci.arsw.code_arena.repository

import edu.eci.arsw.code_arena.model.Room
import edu.eci.arsw.code_arena.model.RoomStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface RoomRepository : MongoRepository<Room, String> {
    
    fun findByRoomCode(roomCode: String): Room?
    
    fun existsByRoomCode(roomCode: String): Boolean
    
    fun findByHostId(hostId: String): List<Room>
    
    fun findByStatus(status: RoomStatus): List<Room>
    
    fun findByIsPrivateFalseAndStatus(status: RoomStatus): List<Room>
    
    @Query("{ 'players.userId': ?0 }")
    fun findByPlayerId(playerId: String): List<Room>
    
    @Query("{ 'currentPlayers.userId': ?0, 'status': ?1 }")
    fun findByCurrentPlayersUserIdAndStatus(userId: String, status: RoomStatus): List<Room>
    
    fun findByConfigIsPublicTrueAndStatusIn(statuses: List<RoomStatus>): List<Room>
    
    fun findByStatusInAndExpiresAtAfter(statuses: List<RoomStatus>, now: LocalDateTime): List<Room>
    
    fun findByExpiresAtBefore(expiredTime: LocalDateTime): List<Room>
    
    @Query("{ 'config.isPublic': true, 'status': { \$in: ['WAITING', 'READY'] }, \$expr: { \$lt: [{ \$size: '\$players' }, '\$config.maxPlayers'] } }")
    fun findAvailablePublicRooms(): List<Room>
}
