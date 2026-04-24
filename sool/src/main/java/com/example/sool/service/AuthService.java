package com.example.sool.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.UserDto;

@Service
public class AuthService {

    private static final Duration CODE_TTL = Duration.ofMinutes(3);
    private static final Duration COOLDOWN_TTL = Duration.ofSeconds(10);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(5); 

    private static final Duration LOGIN_FAIL_TTL = Duration.ofMinutes(5);
    private static final Duration LOGIN_LOCK_TTL = Duration.ofMinutes(5);
    private static final int MAX_LOGIN_FAIL_COUNT = 5;

    private final UserService userService;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public AuthService(
            UserService userService,
            EmailService emailService,
            StringRedisTemplate redisTemplate
    ) {
        this.userService = userService;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    //아이디 값 존재 확인
    public boolean isLoginIdAvailable(String loginId) {
        
        validateLoginId(loginId);   //유효성 검사

        return userService.findByLoginId(loginId) == null;
    }
    
    //이메일 6자리 랜덤 번호 생성
    private String createAuthCode() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }

    //코드키 확인
    private String getCodeKey(String email) {
        return "email:code:" + email;
    }

    //인증시간 확인
    private String getCooldownKey(String email) {
        return "email:cooldown:" + email;
    }

    //인증 여부 확인
    private String getVerifiedKey(String email) {
        return "email:verified:" + email;
    }

    //로그인 실패 키
    private String getLoginFailKey(String loginId) {
        return "login:fail:" + loginId;
    }

    //로그인 잠금 키
    private String getLoginLockKey(String loginId) {
        return "login:lock:" + loginId;
    }

    public void checkLoginBlocked(String loginId) {
        validateLoginId(loginId);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(getLoginLockKey(loginId)))) {
            long remainSeconds = getLoginLockRemainingSeconds(loginId);
            long minutes = remainSeconds / 60;
            long seconds = remainSeconds % 60;

            if (minutes > 0) {
                throw new IllegalArgumentException(
                    "로그인 실패 횟수를 초과했습니다. \n" + minutes + "분 " + seconds + "초 후 다시 시도해주세요."
                );
            }

            throw new IllegalArgumentException(
                "로그인 실패 횟수를 초과했습니다. \n" + seconds + "초 후 다시 시도해주세요."
            );
        }
    }

    public void increaseLoginFail(String loginId) {
        validateLoginId(loginId);

        String failKey = getLoginFailKey(loginId);
        Long failCount = redisTemplate.opsForValue().increment(failKey);

        if (failCount != null && failCount == 1L) {
            redisTemplate.expire(failKey, LOGIN_FAIL_TTL);
        }

        if (failCount != null && failCount >= MAX_LOGIN_FAIL_COUNT) {
            redisTemplate.delete(failKey);
            redisTemplate.opsForValue().set(getLoginLockKey(loginId), "Y", LOGIN_LOCK_TTL);
        }
    }

    public void clearLoginFail(String loginId) {
        validateLoginId(loginId);

        redisTemplate.delete(getLoginFailKey(loginId));
        redisTemplate.delete(getLoginLockKey(loginId));
    }

    public long getRemainingLoginFailCount(String loginId) {
        validateLoginId(loginId);

        String value = redisTemplate.opsForValue().get(getLoginFailKey(loginId));
        long failCount = value == null ? 0 : Long.parseLong(value);
        long remaining = MAX_LOGIN_FAIL_COUNT - failCount;

        return Math.max(remaining, 0);
    }

    public long getLoginLockRemainingSeconds(String loginId) {
        validateLoginId(loginId);

        Long seconds = redisTemplate.getExpire(getLoginLockKey(loginId));

        if (seconds == null || seconds < 0) {
            return 0;
        }

        return seconds;
    }

    //이메일 인증코드 설정 및 보내기
    private void sendCode(String email) {

        String code = createAuthCode();

        redisTemplate.opsForValue().set(getCodeKey(email), code, CODE_TTL);
        redisTemplate.opsForValue().set(getCooldownKey(email), "Y", COOLDOWN_TTL);

        emailService.sendAuthCode(email, code);
    }

    //최초 이메일 코드 전송
    public void sendEmailCode(String loginId, String name, String email) {

        validateLoginId(loginId);   //로그인 아이디 입력 확인
        validateName(name);         //이름 입력 확인
        validateEmail(email);       //이메일 입력 확인

        if (userService.findByLoginId(loginId) != null) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userService.selectUserByEmail(email) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String cooldownKey = getCooldownKey(email);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new IllegalArgumentException("인증번호는 잠시 후 다시 요청해주세요.");
        }

        sendCode(email);
    }

    //이메일 코드 재전송
    public void resendEmailCode(String email) {
        validateEmail(email);

        String cooldownKey = getCooldownKey(email);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new IllegalArgumentException("인증번호는 잠시 후 다시 요청해주세요.");
        }

        sendCode(email);
    }

    //최초 이메일 코드 전송(아이디 찾기)
    public void sendIdEmailCode(String email) {

        validateEmail(email);

        if (userService.selectUserByEmail(email) == null) {
            throw new IllegalArgumentException("없는 이메일입니다.");
        }

        String cooldownKey = getCooldownKey(email);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new IllegalArgumentException("인증번호는 잠시 후 다시 요청해주세요.");
        }

        sendCode(email);
    }

    //최초 이메일 코드 전송(비밀번호 재설정)
    public void sendPasswordEmailCode(String loginId, String email) {

        validateLoginId(loginId);
        validateEmail(email);

        UserDto user = userService.findByLoginId(loginId);

        if (user == null) {
            throw new IllegalArgumentException("없는 아이디입니다.");
        }

        if (!email.equals(user.getEmail())) {
            throw new IllegalArgumentException("아이디와 이메일이 일치하지 않습니다.");
        }
        
        String cooldownKey = getCooldownKey(email);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new IllegalArgumentException("인증번호는 잠시 후 다시 요청해주세요.");
        }

        sendCode(email);
    }


    public boolean verifyEmailCode(String email, String code) {
        validateEmail(email);

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("인증번호를 입력하세요.");
        }

        String savedCode = redisTemplate.opsForValue().get(getCodeKey(email));

        if (savedCode == null) {
            throw new IllegalArgumentException("인증번호가 만료되었거나 존재하지 않습니다.");
        }

        if (!savedCode.equals(code)) {
            return false;
        }

        redisTemplate.delete(getCodeKey(email));
        redisTemplate.opsForValue().set(getVerifiedKey(email), "Y", VERIFIED_TTL);

        return true;
    }

    @Transactional
    public int signUp(UserDto userDto) {
        validateUserDto(userDto);

        if (userService.findByLoginId(userDto.getLoginId()) != null) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userService.selectUserByEmail(userDto.getEmail()) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String verified = redisTemplate.opsForValue().get(getVerifiedKey(userDto.getEmail()));
        if (!"Y".equals(verified)) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        userDto.setRoleCode("USER");

        int result = userService.insertUser(userDto);

        redisTemplate.delete(getCodeKey(userDto.getEmail()));
        redisTemplate.delete(getCooldownKey(userDto.getEmail()));
        redisTemplate.delete(getVerifiedKey(userDto.getEmail()));

        return result;
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("회원정보가 없습니다.");
        }

        validateLoginId(userDto.getLoginId());
        validateName(userDto.getName());
        validateEmail(userDto.getEmail());

        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력하세요.");
        }
    }

    //입력 확인
    private void validateLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("아이디를 입력하세요.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력하세요.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력하세요.");
        }
    }

    //아이디 찾기
    public String findLoginId(String email) {
        validateEmail(email);

        String verified = redisTemplate.opsForValue().get(getVerifiedKey(email));
        if (!"Y".equals(verified)) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        UserDto dto = new UserDto();
        dto.setEmail(email);
        UserDto user = userService.findByEmail(dto);


        redisTemplate.delete(getVerifiedKey(email));

        return user.getLoginId();
    }


    //비밀번호 재설정
    public void resetPassword(UserDto userDto) {

        validateLoginId(userDto.getLoginId());
        validateEmail(userDto.getEmail());

        UserDto user = userService.findByLoginId(userDto.getLoginId());

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        }

        if (!userDto.getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("아이디와 이메일이 일치하지 않습니다.");
        }

        String verified = redisTemplate.opsForValue().get(getVerifiedKey(userDto.getEmail()));

        if (!"Y".equals(verified)) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        userDto.setUserId(user.getUserId());

        userService.updateUserPassword(userDto);

        redisTemplate.delete(getVerifiedKey(userDto.getEmail()));
    }
}