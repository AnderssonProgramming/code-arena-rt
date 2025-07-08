package edu.eci.arsw.code_arena.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    
    @Indexed(unique = true)
    val username: String,
    
    @Indexed(unique = true)
    val email: String,
    
    val passwordHash: String,
    
    val profile: UserProfile = UserProfile(),
    
    val stats: UserStats = UserStats(),
    
    val settings: UserSettings = UserSettings(),
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    val lastLoginAt: LocalDateTime? = null,
    
    val isActive: Boolean = true
)

data class UserProfile(
    val displayName: String? = null,
    val avatar: String? = null,
    val level: Int = 1,
    val experience: Int = 0
)

data class UserStats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val averageScore: Double = 0.0,
    val totalPlayTime: Long = 0L, // En segundos
    val bestStreak: Int = 0,
    val currentStreak: Int = 0
) {
    val winRate: Double
        get() = if (gamesPlayed > 0) gamesWon.toDouble() / gamesPlayed else 0.0
}

data class UserSettings(
    val notifications: Boolean = true,
    val soundEnabled: Boolean = true,
    val theme: String = "light"
)
