package com.mobApp.roulette.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.Customizer

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
                // CSRF 비활성화 (Spring Security 6.1+ 방식)
                .csrf { it.disable() }
                // 모든 요청 허용
                .authorizeHttpRequests { auth ->
                    auth.anyRequest().permitAll()
                }
                // 기본 로그인 화면 제거
                .formLogin { it.disable() }
                // HTTP Basic 인증 비활성화
                .httpBasic { it.disable() }

        return http.build()
    }
}
