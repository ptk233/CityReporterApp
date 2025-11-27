package com.example.cityreporter.dto

import com.example.cityreporter.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

// DTO dla rejestracji użytkownika
data class RegisterRequest(
    @field:NotBlank(message = "Email nie może być pusty")
    @field:Email(message = "Niepoprawny format email")
    val email: String,
    
    @field:NotBlank(message = "Hasło nie może być puste")
    @field:Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Hasło musi zawierać małe i duże litery oraz cyfrę"
    )
    val password: String,
    
    @field:NotBlank(message = "Imię i nazwisko nie może być puste")
    val name: String,
    
    @field:Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Niepoprawny format numeru telefonu"
    )
    val phoneNumber: String? = null
)

// DTO dla logowania
data class LoginRequest(
    @field:NotBlank(message = "Email nie może być pusty")
    @field:Email(message = "Niepoprawny format email")
    val email: String,
    
    @field:NotBlank(message = "Hasło nie może być puste")
    val password: String
)

// DTO dla odpowiedzi po logowaniu
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: UserResponse
)

// DTO z informacjami o użytkowniku
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String?,
    val role: UserRole,
    val points: Int,
    val isActive: Boolean
)

// DTO dla aktualizacji profilu użytkownika
data class UpdateUserRequest(
    val name: String? = null,
    val phoneNumber: String? = null
)

// DTO dla zmiany hasła
data class ChangePasswordRequest(
    @field:NotBlank(message = "Stare hasło nie może być puste")
    val oldPassword: String,
    
    @field:NotBlank(message = "Nowe hasło nie może być puste")
    @field:Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Hasło musi zawierać małe i duże litery oraz cyfrę"
    )
    val newPassword: String
)
