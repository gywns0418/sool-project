package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.TastingNoteMetricDto;

@Mapper
public interface TastingNoteMetricMapper {

    //맛 프로파일 등록
    int insertMetric(TastingNoteMetricDto tastingNoteMetricDto);

    //맛 프로파일 수정
    int updateMetric(TastingNoteMetricDto tastingNoteMetricDto);
    
    //특정 노트의 맛 점수 조회
    List<TastingNoteMetricDto> findByNoteId(Integer noteId);

    //주류 디테일 평균 맛 프로파일
    List<TastingNoteMetricDto> findAvgMetricByDrinkId(Integer drinkId);

    //노트 전체 맛 점수 삭제
    int deleteByNoteId(Integer noteId);

    //회원 삭제 시 맛 점수 삭제
    int deleteByUserId(Integer userId);
}