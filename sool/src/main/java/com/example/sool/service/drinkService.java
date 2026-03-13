package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.DrinkDto;
import com.example.sool.mapper.DrinkMapper;

@Service
public class DrinkService {

    private final DrinkMapper drinkMapper;

    public DrinkService(DrinkMapper drinkMapper) {
        this.drinkMapper = drinkMapper;
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

    public DrinkDto searchDrinkList(String keyword) {
        return drinkMapper.searchDrinkList(keyword);
    }

    public int updateDrink(DrinkDto drinkDTO) {
        return drinkMapper.updateDrink(drinkDTO);
    }

    public int deleteDrink(Integer drinkId) {
        return drinkMapper.deleteDrink(drinkId);
    }
}