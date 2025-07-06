package edu.eci.arsw.code_arena.repository

import edu.eci.arsw.code_arena.model.Challenge
import edu.eci.arsw.code_arena.model.ChallengeType
import edu.eci.arsw.code_arena.model.Difficulty
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChallengeRepository : MongoRepository<Challenge, String> {
    
    fun findByType(type: ChallengeType): List<Challenge>
    
    fun findByDifficulty(difficulty: Difficulty): List<Challenge>
    
    fun findByTypeAndDifficulty(type: ChallengeType, difficulty: Difficulty): List<Challenge>
    
    fun findByIsActiveTrue(): List<Challenge>
    
    fun findByIsActiveTrueOrderByCreatedAtDesc(): List<Challenge>
    
    fun findByDifficultyAndIsActiveTrue(difficulty: Difficulty): List<Challenge>
    
    fun findByTypeAndIsActiveTrue(type: ChallengeType): List<Challenge>
    
    fun findByTypeAndDifficultyAndIsActiveTrue(type: ChallengeType, difficulty: Difficulty): List<Challenge>
    
    fun findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsActiveTrue(
        title: String, 
        description: String
    ): List<Challenge>
    
    @Query("{ 'tags': { \$in: ?0 }, 'isActive': true }")
    fun findByTagsInAndIsActiveTrue(tags: List<String>): List<Challenge>
    
    @Query("{ 'difficulty': ?0, 'isActive': true }")
    fun findRandomByDifficultyAndIsActiveTrue(difficulty: Difficulty): List<Challenge>
    
    fun findByCreatedBy(createdBy: String): List<Challenge>
    
    @Query("{ 'stats.successRate': { \$gte: ?0, \$lte: ?1 }, 'isActive': true }")
    fun findBySuccessRateBetween(minRate: Double, maxRate: Double): List<Challenge>
}
