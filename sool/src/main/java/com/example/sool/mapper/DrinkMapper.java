package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.DrinkDto;


@Mapper
public interface DrinkMapper {

    int insertDrink(DrinkDto drinkDTO);

    List<DrinkDto> DrinkAll();

    DrinkDto findByDrinkId(Integer drinkId);

    int updateDrink(DrinkDto drinkDTO);

    int deleteDrink(Integer drinkId);
}