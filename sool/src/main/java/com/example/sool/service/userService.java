package com.example.sool.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.UserDto;
import com.example.sool.mapper.CommentMapper;
import com.example.sool.mapper.ImageMapper;
import com.example.sool.mapper.LikeMapper;
import com.example.sool.mapper.TastingNoteMapper;
import com.example.sool.mapper.TastingNoteMetricMapper;
import com.example.sool.mapper.UserMapper;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final TastingNoteMetricMapper tastingNoteMetricMapper;
    private final TastingNoteMapper tastingNoteMapper;
    private final ImageMapper imageMapper;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder, LikeMapper likeMapper, CommentMapper commentMapper,
                        TastingNoteMetricMapper tastingNoteMetricMapper, TastingNoteMapper tastingNoteMapper,ImageMapper imageMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder ;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
        this.tastingNoteMetricMapper = tastingNoteMetricMapper;
        this.tastingNoteMapper = tastingNoteMapper;
        this.imageMapper = imageMapper;
    }

    public UserDto findByLoginId(String loginId) {
        return userMapper.findByLoginId(loginId);
    }

    public UserDto findByEmail(UserDto dto){
        return userMapper.findByEmail(dto);
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
    
    public int updateUserPassword(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.updateUserPassword(userDto);
    }


    //유저 삭제
    @Transactional
    public int deleteUser(int userId) {
        //좋아요 삭제
        likeMapper.deleteByUserId(userId);

        //회원 삭제
        return userMapper.deleteUser(userId);
    }

    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email) > 0;
    }

    public int updateUser(UserDto dto){
        return userMapper.updateUser(dto);
    }
}