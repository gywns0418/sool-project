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

    //필터(카테고리, 도수, 가격)
    List<DrinkDto> getFilterList(DrinkSearchDto drinkSearchDto);

    //필터 총 갯수
    int getFilterCount(DrinkSearchDto drinkSearchDto);

    int insertDrink(DrinkDto drinkDTO);

    List<DrinkDto> drinkAll();

    DrinkDto findByDrinkId(Integer drinkId);

    int updateDrink(DrinkDto drinkDTO);

    int deleteDrink(Integer drinkId);
}