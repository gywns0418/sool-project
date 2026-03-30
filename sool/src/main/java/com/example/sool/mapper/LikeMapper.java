package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.LikeDto;
import com.example.sool.dto.TastingNoteDto;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface LikeMapper {
    
    int insertLike(LikeDto likeDto);

    int existsLike(LikeDto likeDto);

    int deleteLike(LikeDto likeDto);

    int deleteAllLike(@Param("objType") String objType, @Param("objId") int objId);

    List<DrinkDto> findLikedDrinkList(Integer userId);

    List<TastingNoteDto> findLikedNoteList(Integer userId);

    int deleteByUserId(Integer userId);
}
