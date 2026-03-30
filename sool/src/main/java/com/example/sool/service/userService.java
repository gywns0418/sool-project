package com.example.sool.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.UserDto;
import com.example.sool.mapper.UserMapper;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder ;
    }

    public UserDto findByLoginId(String loginId) {
        return userMapper.findByLoginId(loginId);
    }

    public UserDto findByNameAndEmail(UserDto dto){
        return userMapper.findByNameAndEmail(dto);
    }

    public int insertUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.insertUser(userDto);
    }

    public UserDto selectUserById(int userId) {
        return userMapper.selectUserById(userId);
    }

    public UserDto selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }


    public UserDto getMyPageSidebar(Integer userId) {
        return userMapper.selectMyPageSidebar(userId);
    }


    //마이페이지 비밀번호 재설정
    @Transactional
    public void updateMyPassword(int userId, String currentPassword, String newPassword) {

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호를 입력해주세요.");
        }

        String encodedPassword = userMapper.findPasswordByUserId(userId);

        if (!passwordEncoder.matches(currentPassword, encodedPassword)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        
        UserDto dto = new UserDto();
        dto.setUserId(userId);
        dto.setPassword(passwordEncoder.encode(newPassword));
        
        userMapper.updateUserPassword(dto);
    }





    public List<UserDto> selectUserList() {
        return userMapper.selectUserList();
    }

    public int updateUser(UserDto userDto) {
        return userMapper.updateUser(userDto);
    }

    public int updateUserPassword(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.updateUserPassword(userDto);
    }

    public int deleteUser(int userId) {
        return userMapper.deleteUser(userId);
    }
}