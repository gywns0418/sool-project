package com.example.sool.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.sool.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 로그인 시 사용자 조회에 사용할 서비스
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Vue 프론트 요청 허용
            .cors(Customizer.withDefaults())

            // 세션 기반 REST API 방식 ->  csrf 비활성화
            .csrf(csrf -> csrf.disable())
            // 스프링 기본 로그인 페이지 사용 안 함
            .formLogin(form -> form.disable())
            // http basic 인증 사용 안 함
            .httpBasic(basic -> basic.disable())

            // 세션 기반 인증 설정
            .sessionManagement(session -> session
                // 필요할 때만 세션 생성
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // 동일 계정 동시 로그인 최대 1개
                .maximumSessions(1)
                // 기존 로그인 있으면 새 로그인 허용
                .maxSessionsPreventsLogin(false)
            )

            .authorizeHttpRequests(auth -> auth

                // 인증 없이 접근 가능
                .requestMatchers(
                    "/api/auth/**"
                ).permitAll()

                // 조회는 비회원도 가능
                .requestMatchers(HttpMethod.GET,
                    "/api/home",
                    "/api/drinks/**",
                    "/api/notes/**",
                    "/api/comments/**"
                ).permitAll()

                // 로그인한 USER 권한만 가능
                .requestMatchers(
                    "/api/notes/**",
                    "/api/comments/**",
                    "/api/likes/**",
                    "/api/mypage/**",
                    "/api/drinks/*/like",
                    "/api/report/**",
                    "/api/image/**"
                ).hasAnyRole("USER", "ADMIN")

                // 그 외 요청은 인증 필요
                .anyRequest().authenticated()
            );

        return http.build();
    }

    //아이디로 사용자 조회 / 암호화된 비밀번호 비교 *인증담당*
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        //UserDetailsService를 생성자에 바로 전달
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);

        // 비밀번호 비교 시 BCrypt 사용
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        //아이디 에러 숨김 X
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    //로그인 컨트롤러에서 사용할 객체 생성
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // AuthController에서 사용할 AuthenticationManager 등록
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Vue 서버 주소 허용
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));
        // 세션 쿠키 전달 허용
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}