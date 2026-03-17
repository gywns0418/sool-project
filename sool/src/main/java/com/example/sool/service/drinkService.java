package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
import com.example.sool.mapper.DrinkMapper;

@Service
public class DrinkService {

    private final DrinkMapper drinkMapper;

    public DrinkService(DrinkMapper drinkMapper) {
        this.drinkMapper = drinkMapper;
    }


    //인기술 top4
    public List<DrinkDto> drinkTop() {
        return drinkMapper.drinkTop();
    }

    //검색
    public List<DrinkDto> searchDrinkList(DrinkSearchDto drinkSearchDto) {
        return drinkMapper.searchDrinkList(drinkSearchDto);
    }

    //검색 총 갯수
    public int searchDrinkCount(DrinkSearchDto drinkSearchDto) {
        return drinkMapper.searchDrinkCount(drinkSearchDto);
    }

    //필터(카테고리, 도수, 가격)
    public List<DrinkDto> getFilterList(DrinkSearchDto drinkSearchDto) {
        return drinkMapper.getFilterList(drinkSearchDto);
    }

    //필터 총 갯수
    public int getFilterCount(DrinkSearchDto drinkSearchDto) {
        return drinkMapper.getFilterCount(drinkSearchDto);
    }

    public int insertDrink(DrinkDto drinkDTO) {
        return drinkMapper.insertDrink(drinkDTO);
    }

    public List<DrinkDto> drinkAll() {
        return drinkMapper.drinkAll();
    }

    public DrinkDto findByDrinkId(Integer drinkId) {
        return drinkMapper.findByDrinkId(drinkId);
    }

    public int updateDrink(DrinkDto drinkDTO) {
        return drinkMapper.updateDrink(drinkDTO);
    }

    public int deleteDrink(Integer drinkId) {
        return drinkMapper.deleteDrink(drinkId);
    }
}