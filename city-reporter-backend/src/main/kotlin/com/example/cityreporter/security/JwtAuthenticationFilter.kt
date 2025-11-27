package com.example.cityreporter.security

import com.example.cityreporter.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        
        logger.info("=== JWT Filter Debug ===")
        logger.info("Request URI: ${request.requestURI}")
        logger.info("Request Method: ${request.method}")
        logger.info("Authorization Header: ${authorizationHeader?.take(50)}...")
        
        var email: String? = null
        var jwt: String? = null
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7)
            try {
                email = jwtUtil.extractEmail(jwt)
                logger.info("Extracted email from JWT: $email")
            } catch (e: Exception) {
                logger.error("Błąd podczas parsowania tokenu JWT", e)
            }
        } else {
            logger.warn("No valid Authorization header found")
        }
        
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            logger.info("Trying to authenticate user: $email")
            val user = userRepository.findByEmail(email)
            
            if (user != null) {
                logger.info("User found: ${user.email}, Role: ${user.role}")
                val isValid = jwtUtil.validateToken(jwt!!, user)
                logger.info("Token validation result: $isValid")
                
                if (isValid) {
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
                    logger.info("Setting authorities: $authorities")
                    
                    val authToken = UsernamePasswordAuthenticationToken(
                        user.id, // Principal - używamy ID użytkownika
                        null,
                        authorities
                    )
                    
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                    logger.info("Authentication set successfully for user: ${user.id}")
                } else {
                    logger.error("Token validation FAILED for user: ${user.email}")
                }
            } else {
                logger.error("User NOT FOUND for email: $email")
            }
        } else if (email == null) {
            logger.warn("Email is null - cannot authenticate")
        } else {
            logger.info("Authentication already exists in SecurityContext")
        }
        
        logger.info("Current authentication: ${SecurityContextHolder.getContext().authentication}")
        logger.info("=== End JWT Filter Debug ===")
        
        filterChain.doFilter(request, response)
    }
}
