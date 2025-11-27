package com.example.cityreporter.controller

import com.example.cityreporter.dto.*
import com.example.cityreporter.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val user = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }
    
    @PostMapping("/refresh")
    fun refreshToken(@RequestParam("refreshToken") refreshToken: String): ResponseEntity<LoginResponse> {
        val response = authService.refreshToken(refreshToken)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<UserResponse> {
        val userId = authentication.principal as String
        val user = authService.getUserById(userId)
        return ResponseEntity.ok(user)
    }
    
    @PutMapping("/me")
    fun updateProfile(
        authentication: Authentication,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val userId = authentication.principal as String
        val user = authService.updateUser(userId, request)
        return ResponseEntity.ok(user)
    }
    
    @PostMapping("/change-password")
    fun changePassword(
        authentication: Authentication,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Map<String, String>> {
        val userId = authentication.principal as String
        authService.changePassword(userId, request)
        return ResponseEntity.ok(mapOf("message" to "Hasło zostało zmienione"))
    }
    
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        // Token jest bezstanowy, więc wylogowanie odbywa się po stronie klienta
        // Tutaj możemy dodać token do blacklisty jeśli potrzeba
        return ResponseEntity.ok(mapOf("message" to "Wylogowano pomyślnie"))
    }
}
