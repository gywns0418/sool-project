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