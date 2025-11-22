package com.mobApp.roulette.service

import com.mobApp.roulette.domain.User
import com.mobApp.roulette.dto.*
import com.mobApp.roulette.repository.UserRepository
import com.mobApp.roulette.util.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository,
        private val jwtUtil: JwtUtil
){
    private val encoder = BCryptPasswordEncoder()

    @Transactional
    fun signUp(req: SignUpRequest): SignUpResponse{
        if (userRepository.existsByUserId(req.userId)) {
            throw IllegalArgumentException("이미 존재하는 userId 입니다.")
        }
        val user = User(
                userId = req.userId,
                passwordHash = encoder.encode(req.password),
                nickname = req.nickname
        )
        val saved = userRepository.save(user)
        return SignUpResponse(saved.id!!, saved.userId, saved.nickname)
    }

    fun login(req: LoginRequest): LoginResponse{
        val optional = userRepository.findByUserId(req.userId)
        val user = optional.orElseThrow{ IllegalArgumentException("존재하지 않는 사용자입니다.") }
        if (!encoder.matches(req.password, user.passwordHash)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }
        val accessToken = jwtUtil.generateAccessToken(user.id!!, user.userId)
        val refreshToken = "refresh-token-placeholder" // TODO: 구현
        return LoginResponse(accessToken, refreshToken, user.id!!, user.nickname)

    }
    fun refreshToken(req: TokenRefreshRequest): TokenRefreshResponse {
        // TODO: refresh token 검증 로직 구현
        val newAccess = jwtUtil.generateAccessToken(0, "unknown")
        return TokenRefreshResponse(newAccess)
    }
    fun findById(id: Long): Optional<User> = userRepository.findById(id) //////필요한지 체크

}