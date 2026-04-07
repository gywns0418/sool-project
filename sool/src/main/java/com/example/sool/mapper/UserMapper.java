package com.example.sool.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.UserDto;

@Mapper
public interface UserMapper {

    //로그인 id로 회원 정보
    UserDto findByLoginId(String loginId);

    //회원가입
    int insertUser(UserDto userDto);

    //회원 단건 조회
    UserDto selectUserById(int userId);

    //이름과 이메일로 회원 정보
    UserDto findByEmail(UserDto dto);

    //이메일로 회원 정보
    UserDto selectUserByEmail(String email);

    //회원 비밀번호 재설정
    int updateUserPassword(UserDto userDto);

    //마이페이지 사이드바
    UserDto selectMyPageSidebar(int userId);

    //현재 비밀번호 일치 확인
    String findPasswordByUserId(int userId);

    //회원 정보 수정
    int updateUser(UserDto userDto);

    //회원 탈퇴
    int deleteUser(int userId);

    int existsByEmail(String email);
}