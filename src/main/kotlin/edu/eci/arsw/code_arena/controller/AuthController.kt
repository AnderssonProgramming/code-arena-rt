package edu.eci.arsw.code_arena.controller

import edu.eci.arsw.code_arena.dto.*
import edu.eci.arsw.code_arena.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for authentication and user management
 * 
 * @author Andersson David Sánchez Méndez
 * @since 2025-01-05
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:4200"])
class AuthController(
    private val userService: UserService
) {

    /**
     * Register a new user
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = userService.registerUser(registerRequest)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = authResponse,
                    message = "User registered successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Registration failed")
                )
            )
        }
    }

    /**
     * Authenticate user login
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return try {
            val authResponse = userService.loginUser(loginRequest)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = authResponse,
                    message = "Login successful"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Authentication failed")
                )
            )
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<ApiResponse<UserDto>> {
        return try {
            val username = authentication.name
            val user = userService.getUserByUsername(username)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = user,
                    message = "User profile retrieved"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "User not found")
                )
            )
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    fun updateProfile(
        authentication: Authentication,
        @Valid @RequestBody updateRequest: UpdateProfileRequest
    ): ResponseEntity<ApiResponse<UserDto>> {
        return try {
            val username = authentication.name
            val currentUser = userService.getUserByUsername(username)
            val updatedUser = userService.updateUserProfile(currentUser.id, updateRequest)
            
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = updatedUser,
                    message = "Profile updated successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Profile update failed")
                )
            )
        }
    }

    /**
     * Check username availability
     */
    @GetMapping("/check-username/{username}")
    fun checkUsernameAvailability(@PathVariable username: String): ResponseEntity<ApiResponse<Boolean>> {
        val isAvailable = userService.isUsernameAvailable(username)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = isAvailable,
                message = if (isAvailable) "Username available" else "Username already taken"
            )
        )
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email/{email}")
    fun checkEmailAvailability(@PathVariable email: String): ResponseEntity<ApiResponse<Boolean>> {
        val isAvailable = userService.isEmailAvailable(email)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = isAvailable,
                message = if (isAvailable) "Email available" else "Email already registered"
            )
        )
    }

    /**
     * Get user rankings
     */
    @GetMapping("/rankings")
    fun getUserRankings(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<ApiResponse<List<UserDto>>> {
        return try {
            val rankings = userService.getUserRankings(limit)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = rankings,
                    message = "Rankings retrieved successfully"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Failed to retrieve rankings")
                )
            )
        }
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    fun searchUsers(
        @RequestParam query: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<List<UserDto>>> {
        return try {
            val users = userService.searchUsers(query, limit)
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = users,
                    message = "Users found"
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse(
                    success = false,
                    message = e.message,
                    errors = listOf(e.message ?: "Search failed")
                )
            )
        }
    }
}
