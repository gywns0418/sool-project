package com.example.sool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                //로그인, 회원가입, 아이디 찾기, 비밀번호 재설정
                .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/signup"
                ).permitAll()

                //조회 (비회원)
                .requestMatchers(HttpMethod.GET,
                        "/api/drinks/**",
                        "/api/notes/**",
                        "/api/comments/**"
                ).permitAll()

                //로그인 필요
                .requestMatchers(
                        "/api/auth/logout",
                        "/api/notes/**",
                        "/api/comments/**",
                        "/api/likes/**",
                        "/api/mypage/**"
                ).hasRole("USER")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}