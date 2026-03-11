package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.TastingNoteMetricDto;

@Mapper
public interface TastingNoteMetricMapper {

    int insertMetric(TastingNoteMetricDto tastingNoteMetricDto);

    List<TastingNoteMetricDto> findByNoteId(Integer noteId);

    int updateMetric(TastingNoteMetricDto tastingNoteMetricDto);

    int deleteByNoteId(Integer noteId);

    List<TastingNoteMetricDto> findByMetricCode(String metricCode);
}