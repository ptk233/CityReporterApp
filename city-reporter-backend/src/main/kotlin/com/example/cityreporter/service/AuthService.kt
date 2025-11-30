package com.example.cityreporter.service

import com.example.cityreporter.dto.*
import com.example.cityreporter.entity.User
import com.example.cityreporter.entity.UserRole
import com.example.cityreporter.repository.UserRepository
import com.example.cityreporter.security.JwtUtil
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    
    fun register(request: RegisterRequest): UserResponse {
        // Walidacja email
        if (!request.email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            throw IllegalArgumentException("Nieprawidłowy format adresu email")
        }
        
        // Sprawdź czy email już istnieje
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email jest już używany")
        }
        
        // Walidacja hasła
        if (request.password.length < 8) {
            throw IllegalArgumentException("Hasło musi mieć minimum 8 znaków")
        }
        if (!request.password.matches(Regex(".*[a-z].*"))) {
            throw IllegalArgumentException("Hasło musi zawierać małą literę")
        }
        if (!request.password.matches(Regex(".*[A-Z].*"))) {
            throw IllegalArgumentException("Hasło musi zawierać dużą literę")
        }
        if (!request.password.matches(Regex(".*\\d.*"))) {
            throw IllegalArgumentException("Hasło musi zawierać cyfrę")
        }
        
        // Walidacja imienia
        if (request.name.isBlank() || request.name.length < 3) {
            throw IllegalArgumentException("Imię i nazwisko musi mieć minimum 3 znaki")
        }
        
        val rawPassword = request.password
        val encodedPassword = passwordEncoder.encode(rawPassword)
        
        println("=== REGISTER DEBUG ===")
        println("Raw password: $rawPassword")
        println("Encoded password: $encodedPassword")
        println("Encoded password length: ${encodedPassword.length}")
        println("=== END REGISTER DEBUG ===")
        
        val user = User(
            email = request.email,
            password = encodedPassword,
            name = request.name,
            phoneNumber = request.phoneNumber,
            role = UserRole.CITIZEN
        )
        
        val savedUser = userRepository.save(user)
        println("User saved with password: ${savedUser.password}")
        return toUserResponse(savedUser)
    }
    
    fun login(request: LoginRequest): LoginResponse {
        println("=== LOGIN DEBUG START ===")
        println("Attempting login for email: '${request.email}'")
        println("Email length: ${request.email.length}")
        println("Email bytes: ${request.email.toByteArray().contentToString()}")
        
        val user = userRepository.findByEmail(request.email)
        
        if (user == null) {
            println("USER NOT FOUND for email: '${request.email}'")
            println("Checking all users in database...")
            val allUsers = userRepository.findAll()
            println("Total users in DB: ${allUsers.size}")
            allUsers.forEach { u ->
                println("  - Email: '${u.email}', ID: ${u.id}")
            }
            throw BadCredentialsException("Nieprawidłowy email lub hasło")
        }
        
        println("USER FOUND!")
        println("Email: ${request.email}")
        println("Raw password from request: ${request.password}")
        println("Stored password hash: ${user.password}")
        println("Stored password hash length: ${user.password.length}")
        
        val matches = passwordEncoder.matches(request.password, user.password)
        println("Password matches: $matches")
        println("=== END LOGIN DEBUG ===")
        
        if (!matches) {
            throw BadCredentialsException("Nieprawidłowy email lub hasło")
        }
        
        if (!user.isActive) {
            throw IllegalStateException("Konto jest nieaktywne")
        }
        
        val token = jwtUtil.generateToken(user)
        val refreshToken = jwtUtil.generateRefreshToken(user)
        
        return LoginResponse(
            token = token,
            refreshToken = refreshToken,
            user = toUserResponse(user)
        )
    }
    
    fun getUserById(userId: String): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Użytkownik nie znaleziony") }
        return toUserResponse(user)
    }
    
    fun updateUser(userId: String, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Użytkownik nie znaleziony") }
        
        val updatedUser = user.copy(
            name = request.name ?: user.name,
            phoneNumber = request.phoneNumber ?: user.phoneNumber
        )
        
        val savedUser = userRepository.save(updatedUser)
        return toUserResponse(savedUser)
    }
    
    fun changePassword(userId: String, request: ChangePasswordRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Użytkownik nie znaleziony") }
        
        if (!passwordEncoder.matches(request.oldPassword, user.password)) {
            throw BadCredentialsException("Nieprawidłowe stare hasło")
        }
        
        val updatedUser = user.copy(
            password = passwordEncoder.encode(request.newPassword)
        )
        
        userRepository.save(updatedUser)
    }
    
    fun refreshToken(refreshToken: String): LoginResponse {
        val email = jwtUtil.extractEmail(refreshToken)
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Użytkownik nie znaleziony")
        
        if (!jwtUtil.validateToken(refreshToken, user)) {
            throw BadCredentialsException("Nieprawidłowy refresh token")
        }
        
        val newToken = jwtUtil.generateToken(user)
        val newRefreshToken = jwtUtil.generateRefreshToken(user)
        
        return LoginResponse(
            token = newToken,
            refreshToken = newRefreshToken,
            user = toUserResponse(user)
        )
    }
    
    private fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.email,
            name = user.name,
            phoneNumber = user.phoneNumber,
            role = user.role,
            points = user.points,
            isActive = user.isActive
        )
    }
}
