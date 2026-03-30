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

    //회원 비밀번호 재설정
    int updateUserPassword(UserDto userDto);

    //마이페이지 사이드바
    UserDto selectMyPageSidebar(int userId);

    //현재 비밀번호 일치 확인
    String findPasswordByUserId(int userId);


    List<UserDto> selectUserList();

    int updateUser(UserDto userDto);



    int deleteUser(int userId);
}