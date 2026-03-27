package com.example.sool.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
import com.example.sool.mapper.DrinkMapper;

@Service
public class DrinkService {

    private final DrinkMapper drinkMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public DrinkService(DrinkMapper drinkMapper,RedisTemplate<String, Object> redisTemplate) {
        this.drinkMapper = drinkMapper;
        this.redisTemplate = redisTemplate;
    }


    //인기술 top4 (Redis 사용)
    public List<DrinkDto> drinkTop() {

        String key = "drink:top4";
        
        List<DrinkDto> cached = (List<DrinkDto>) redisTemplate.opsForValue().get(key);
        
        if(cached != null){
            return cached;
        }

        List<DrinkDto> list = drinkMapper.drinkTop();

        redisTemplate.opsForValue().set(key, list, Duration.ofMinutes(10));

        return list;
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

    //주류 디테일 이동
    public DrinkDto findByDrinkId(Integer drinkId) {
        return drinkMapper.findByDrinkId(drinkId);
    }

    //노트 아이디로 주류 정보
    public DrinkDto findDrinkByNoteId(Integer noteId){
        return drinkMapper.findByDrinkId(noteId);
    }











    public int insertDrink(DrinkDto drinkDTO) {
        return drinkMapper.insertDrink(drinkDTO);
    }

    public List<DrinkDto> drinkAll() {
        return drinkMapper.drinkAll();
    }



    public int updateDrink(DrinkDto drinkDTO) {
        return drinkMapper.updateDrink(drinkDTO);
    }

    public int deleteDrink(Integer drinkId) {
        return drinkMapper.deleteDrink(drinkId);
    }
}