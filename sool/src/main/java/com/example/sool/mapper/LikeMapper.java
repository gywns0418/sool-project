package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.LikeDto;

@Mapper
public interface LikeMapper {
    
    int insertLike(LikeDto likeDto);

    int existsLike(LikeDto likeDto);

    int deleteLike(LikeDto likeDto);

    List<LikeDto> findByUserId(Integer userId);
}
