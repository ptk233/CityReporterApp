package com.example.cityreporter.security

import com.example.cityreporter.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {
    
    @Value("\${jwt.secret}")
    private lateinit var secret: String
    
    @Value("\${jwt.expiration}")
    private var expiration: Long = 0
    
    private val refreshExpiration = 604800000L // 7 dni
    
    fun generateToken(user: User): String {
        val claims = HashMap<String, Any>()
        claims["userId"] = user.id
        claims["email"] = user.email
        claims["role"] = user.role.name
        return createToken(claims, user.email)
    }
    
    fun generateRefreshToken(user: User): String {
        val claims = HashMap<String, Any>()
        return createToken(claims, user.email, refreshExpiration)
    }
    
    private fun createToken(claims: Map<String, Any>, subject: String, customExpiration: Long = expiration): String {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + customExpiration))
            .signWith(getSignKey())
            .compact()
    }
    
    fun validateToken(token: String, user: User): Boolean {
        return try {
            val email = extractEmail(token)
            val isExpired = isTokenExpired(token)
            val isEmailValid = email == user.email
            
            println("=== JWT Validation Debug ===")
            println("Token email: $email")
            println("User email: ${user.email}")
            println("Email valid: $isEmailValid")
            println("Token expired: $isExpired")
            println("=== End Validation Debug ===")
            
            isEmailValid && !isExpired
        } catch (e: Exception) {
            println("Token validation error: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    fun extractEmail(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }
    
    fun extractUserId(token: String): String {
        val claims = extractAllClaims(token)
        return claims["userId"] as String
    }
    
    fun extractRole(token: String): String {
        val claims = extractAllClaims(token)
        return claims["role"] as String
    }
    
    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }
    
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
    
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }
    
    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }
    
    private fun getSignKey(): javax.crypto.SecretKey {
        val keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(secret.toByteArray()))
        return Keys.hmacShaKeyFor(keyBytes)
    }
}
