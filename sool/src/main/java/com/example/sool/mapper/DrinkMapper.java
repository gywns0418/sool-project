package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;


@Mapper
public interface DrinkMapper {
    //인기술 top4
    List<DrinkDto> drinkTop();

    //검색
    List<DrinkDto> searchDrinkList(DrinkSearchDto drinkSearchDto);

    //검색 총 갯수
    int searchDrinkCount(DrinkSearchDto drinkSearchDto);

    //주류 디테일 이동
    DrinkDto findByDrinkId(Integer drinkId);

    //노트 아이디로 주류 정보
    DrinkDto findDrinkByNoteId(Integer noteId);
}