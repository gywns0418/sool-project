package com.example.sool.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.LoginRequestDto;
import com.example.sool.dto.LoginResponseDto;
import com.example.sool.dto.UserDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequestDto dto, HttpServletRequest request) {
        try {
            // 아이디 / 비밀번호 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getLoginId(), dto.getPassword())
            );

            //인증 정보를 SecurityContext에 저장
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            //세션 저장
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            LoginResponseDto response = new LoginResponseDto(
                userDetails.getUserId(),
                userDetails.getLoginId(),
                userDetails.getName(),
                userDetails.getRoleCode()
            );

            return ResponseEntity.ok(response);

        }catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("탈퇴한 회원입니다.");
        }catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 상태가 아닙니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LoginResponseDto response = new LoginResponseDto(
            userDetails.getUserId(),
            userDetails.getLoginId(),
            userDetails.getName(),
            userDetails.getRoleCode()
        );

        return ResponseEntity.ok(response);
    }

    //아이디 확인
    @GetMapping("/check-login-id")
    public ResponseEntity<Map<String, Object>> checkLoginId(@RequestParam String loginId) {
        System.out.println(loginId);
        boolean available = authService.isLoginIdAvailable(loginId);

        Map<String, Object> result = new HashMap<>();
        result.put("available", available);

        return ResponseEntity.ok(result);
    }


    //처음 이메일 발송(회원가입)
    @PostMapping("/email/send-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody Map<String, String> body) {

        authService.sendEmailCode(
                body.get("loginId"),
                body.get("name"),
                body.get("email")
        );

        Map<String, Object> result = new HashMap<>();
        result.put("message", "인증번호가 발송되었습니다.");

        return ResponseEntity.ok(result);
    }

    //이메일 재발송(회원가입)
    @PostMapping("/email/resend-code")
    public ResponseEntity<Map<String, Object>> resendEmailCode(@RequestBody Map<String, String> body) {

        authService.resendEmailCode(body.get("email"));

        Map<String, Object> result = new HashMap<>();
        result.put("message", "인증번호가 재발송되었습니다.");

        return ResponseEntity.ok(result);
    }

    //이메일 인증코드 확인
    @PostMapping("/email/verify-code")
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@RequestBody Map<String, String> body) {

        boolean verified = authService.verifyEmailCode(
                body.get("email"),
                body.get("code")
        );

        Map<String, Object> result = new HashMap<>();
        result.put("verified", verified);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserDto userDto) {

        int resultCount = authService.signUp(userDto);

        Map<String, Object> result = new HashMap<>();
        result.put("result", resultCount);
        result.put("message", "회원가입이 완료되었습니다.");

        return ResponseEntity.ok(result);
    }


    //처음 이메일 발송(아이디 찾기)
    @PostMapping("/email/id-send-code")
    public ResponseEntity<Map<String, Object>> sendFindLoginIdEmailCode(@RequestBody Map<String, String> body) {

        authService.sendIdEmailCode(
                body.get("email")
        );

        Map<String, Object> result = new HashMap<>();
        result.put("message", "인증번호가 발송되었습니다.");

        return ResponseEntity.ok(result);
    }

    //아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, Object>> findLoginId(@RequestBody UserDto userDto) {
        String loginId = authService.findLoginId(userDto.getName(), userDto.getEmail());

        Map<String, Object> result = new HashMap<>();
        result.put("loginId", loginId);

        return ResponseEntity.ok(result);
    }

    //처음 이메일 발송(비밀번호 재설정)
    @PostMapping("/email/password-send-code")
    public ResponseEntity<Map<String, Object>> sendResetPasswordEmailCode(@RequestBody Map<String, String> body) {

        authService.sendPasswordEmailCode(
                body.get("loginId"),
                body.get("email")
        );

        Map<String, Object> result = new HashMap<>();
        result.put("message", "인증번호가 발송되었습니다.");

        return ResponseEntity.ok(result);
    }

    //비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestBody UserDto userDto) {

        authService.resetPassword(userDto);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "비밀번호가 변경되었습니다.");

        return ResponseEntity.ok(result);
    }



    //실패 시 메세지
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {

        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());

        return ResponseEntity.badRequest().body(result);
    }

}