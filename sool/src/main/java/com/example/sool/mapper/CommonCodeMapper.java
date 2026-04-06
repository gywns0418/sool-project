package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommonCodeDto;

@Mapper
public interface CommonCodeMapper {

    //홈 카레고리 빠르게 탐색
    List<CommonCodeDto> selectCategoryList();

    //주류 카테고리 리스트 조회
    List<CommonCodeDto> getDrinkCategoryList();

    //신고 사유 가져오기
    List<CommonCodeDto> getReportReasons();

    //카테고리 별 지표 조회
    List<CommonCodeDto> getMetricCode(Integer drinkId);

}