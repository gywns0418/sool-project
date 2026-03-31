package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.LikeDto;
import com.example.sool.dto.TastingNoteDto;

@Mapper
public interface LikeMapper {
    
    //좋아요 추가
    int insertLike(LikeDto likeDto);

    //좋아요 존재 확인
    int existsLike(LikeDto likeDto);

    //좋아요 삭제
    int deleteLike(LikeDto likeDto);

    //마이페이지 좋아요 목록
    List<DrinkDto> findLikedDrinkList(Integer userId);

    //노트 좋아요 찾기
    List<TastingNoteDto> findLikedNoteList(Integer userId);

    //노트 삭제 시 해당 좋아요 삭제
    int deleteAllLike(LikeDto likeDto);

    //회원 삭제 시 해당 좋아요 삭제
    int deleteByUserId(Integer userId);
}
