package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.TastingNoteMetricDto;

@Mapper
public interface TastingNoteMetricMapper {

    //카테고리 별 지표 조회
    List<CommonCodeDto> getMetricCode(Integer drinkId);

    List<TastingNoteMetricDto> findAvgMetricByDrinkId(Integer drinkId);

    int insertMetric(TastingNoteMetricDto tastingNoteMetricDto);

    List<TastingNoteMetricDto> findByNoteId(Integer noteId);

    int updateMetric(TastingNoteMetricDto tastingNoteMetricDto);

    int deleteByNoteId(Integer noteId);

    int deleteByUserId(Integer userId);
}