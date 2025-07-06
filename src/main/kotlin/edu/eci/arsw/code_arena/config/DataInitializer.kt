package edu.eci.arsw.code_arena.config

import edu.eci.arsw.code_arena.service.ChallengeService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * Component to initialize default data on application startup
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@Component
class DataInitializer(
    private val challengeService: ChallengeService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        initializeDefaultChallenges()
    }

    private fun initializeDefaultChallenges() {
        try {
            challengeService.initializeDefaultChallenges()
            println("✅ Default challenges initialized successfully")
        } catch (e: Exception) {
            println("❌ Error initializing default challenges: ${e.message}")
        }
    }
}
