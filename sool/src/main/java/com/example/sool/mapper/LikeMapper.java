package com.example.sool.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.LikeDto;

@Mapper
public interface LikeMapper {
    
    int insertLike(LikeDto likeDto);

    int deleteLike(LikeDto likeDto);

    LikeDto findByUserId(Integer userId);
}
