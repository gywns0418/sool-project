package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.UserDto;

@Mapper
public interface UserMapper {

    UserDto findByLoginId(String loginId);

    int insertUser(UserDto userDto);

    UserDto selectUserById(int userId);

    UserDto selectUserByEmail(String email);

    List<UserDto> selectUserList();

    int updateUser(UserDto userDto);

    int updateUserPassword(UserDto userDto);

    int deleteUser(int userId);
}