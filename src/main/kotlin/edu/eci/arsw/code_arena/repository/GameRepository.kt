package edu.eci.arsw.code_arena.repository

import edu.eci.arsw.code_arena.model.Game
import edu.eci.arsw.code_arena.model.GameStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface GameRepository : MongoRepository<Game, String> {
    
    fun findByRoomId(roomId: String): Game?
    
    fun findByStatus(status: GameStatus): List<Game>
    
    fun findByHostId(hostId: String): List<Game>
    
    @Query("{ 'players.userId': ?0 }")
    fun findByPlayerId(playerId: String): List<Game>
    
    @Query("{ 'players.userId': ?0, 'status': ?1 }")
    fun findByPlayerIdAndStatus(playerId: String, status: GameStatus): List<Game>
    
    fun findByStatusInAndCreatedAtAfter(statuses: List<GameStatus>, createdAt: LocalDateTime): List<Game>
    
    fun findTop10ByStatusOrderByCreatedAtDesc(status: GameStatus): List<Game>
    
    @Query("{ 'finishedAt': { \$gte: ?0, \$lt: ?1 } }")
    fun findFinishedGamesBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Game>
}
