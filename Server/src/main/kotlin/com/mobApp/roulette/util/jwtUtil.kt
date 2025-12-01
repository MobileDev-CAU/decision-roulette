package com.mobApp.roulette.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
object JwtUtil {

    // 1️⃣ 안전한 256비트 이상 키 생성
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    // 2️⃣ Access Token 생성
    fun generateAccessToken(id: Long, userId: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000 * 60 * 60) // 1시간 유효

        return Jwts.builder()
                .setSubject(id.toString())
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)  // 안전한 키 사용
                .compact()
    }

    // 3️⃣ Refresh Token 생성 (선택)
    fun generateRefreshToken(userId: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000L * 60 * 60 * 24 * 7) // 7일 유효

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact()
    }

    // 4️⃣ 토큰 검증 (예시)
    /*fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }*/
}
