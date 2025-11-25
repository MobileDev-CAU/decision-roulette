// JwtUtil.kt
package com.mobApp.roulette.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class JwtUtil {
    // TODO: 실제 시크릿은 application.yml / 환경변수로 관리하세요.
    private val SECRET = System.getenv("JWT_SECRET") ?: "replace_this_in_prod"
    private val ACCESS_TOKEN_EXP_MS = 1000L * 60 * 60 // 1 hour

    fun generateAccessToken(userId: Long, userIdentifier: String): String {
        val claims: Map<String, Any> = HashMap()
        return createToken(claims, userIdentifier, userId)
    }

    private fun createToken(claims: Map<String, Any>, subject: String, userId: Long): String {
        val key = Keys.hmacShaKeyFor(SECRET.toByteArray())
        val now = Date()
        val expiry = Date(now.time + ACCESS_TOKEN_EXP_MS)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("uid", userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
    }

    fun extractAllClaims(token: String): Claims {
        val key = Keys.hmacShaKeyFor(SECRET.toByteArray())
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = extractAllClaims(token)
            claims.expiration.after(Date())
        } catch (e: Exception) {
            false
        }
    }
}
