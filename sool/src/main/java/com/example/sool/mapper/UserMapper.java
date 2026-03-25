package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.UserDto;

@Mapper
public interface UserMapper {

    UserDto findByLoginId(String loginId);

    int insertUser(UserDto userDto);

    UserDto selectUserById(int userId);

    UserDto findByNameAndEmail(UserDto dto);

    UserDto selectUserByEmail(String email);

    //마이페이지 사이드바
    UserDto selectMyPageSidebar(int userId);

    List<UserDto> selectUserList();

    int updateUser(UserDto userDto);

    int updateUserPassword(UserDto userDto);

    int deleteUser(int userId);
}