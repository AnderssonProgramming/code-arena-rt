package edu.eci.arsw.code_arena.repository

import edu.eci.arsw.code_arena.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : MongoRepository<User, String> {
    
    fun findByUsername(username: String): Optional<User>
    
    fun findByEmail(email: String): Optional<User>
    
    fun findByUsernameOrEmail(username: String, email: String): Optional<User>
    
    fun existsByUsername(username: String): Boolean
    
    fun existsByEmail(email: String): Boolean
    
    fun findByIsActiveTrue(): List<User>
    
    fun findByUsernameContainingIgnoreCase(username: String): List<User>
    
    fun findTopUsersByWinRate(limit: Int): List<User>
    
    fun findTop10ByIsActiveTrueOrderByStatsGamesWonDescStatsAverageScoreDesc(): List<User>
}
