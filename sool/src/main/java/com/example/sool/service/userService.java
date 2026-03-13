package com.example.sool.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public int insertUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.insertUser(userDto);
    }

    public UserDto selectUserById(int user_id) {
        return userMapper.selectUserById(user_id);
    }

    public UserDto selectUserByLoginId(String login_id) {
        return userMapper.selectUserByLoginId(login_id);
    }

    public UserDto selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
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

    public int deleteUser(int user_id) {
        return userMapper.deleteUser(user_id);
    }
}