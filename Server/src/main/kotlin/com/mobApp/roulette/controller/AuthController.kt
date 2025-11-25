package com.mobApp.roulette.controller

import com.mobApp.roulette.dto.*
import com.mobApp.roulette.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val userService: UserService){
    @PostMapping("/signup")
    fun signup(@RequestBody req: SignUpRequest): ResponseEntity<SignUpResponse>{
        val res = userService.signUp(req)
        return ResponseEntity.ok(res)
    }
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<LoginResponse>{
        val res = userService.login(req)
        return ResponseEntity.ok(res)
    }
    @PostMapping("refresh")
    fun refresh(@RequestBody req: TokenRefreshRequest): ResponseEntity<TokenRefreshResponse>{
        val res = userService.refreshToken(req)
        return ResponseEntity.ok(res)
    }
}