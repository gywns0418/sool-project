package com.example.sool.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
import com.example.sool.dto.ImageDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.mapper.DrinkMapper;
import com.example.sool.mapper.ImageMapper;

@Service
public class DrinkService {

    private final DrinkMapper drinkMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ImageMapper imageMapper;

    public DrinkService(DrinkMapper drinkMapper,ImageMapper imageMapper,
                            RedisTemplate<String, Object> redisTemplate) {
        this.drinkMapper = drinkMapper;
        this.imageMapper = imageMapper;
        this.redisTemplate = redisTemplate;
    }


    //인기술 top4 (Redis 사용)
    public List<DrinkDto> drinkTop() {

        String key = "drink:top4";
        
        //redis 데이터 확인
        List<DrinkDto> cached = (List<DrinkDto>) redisTemplate.opsForValue().get(key);
        
        if(cached != null){
            return cached;
        }

        //redis에 데이터 없을 때 주류 top4
        List<DrinkDto> list = drinkMapper.drinkTop();
        for (DrinkDto drink : list) {
            ImageDto image = imageMapper.selectImageByDrinkId(drink.getDrinkId());
            drink.setImage(image);
        }

        //redis에 데이터 삽입
        redisTemplate.opsForValue().set(key, list, Duration.ofMinutes(10));

        return list;
    }

    //검색
    public List<DrinkDto> searchDrinkList(DrinkSearchDto drinkSearchDto) {
        List<DrinkDto> list = drinkMapper.searchDrinkList(drinkSearchDto);
        for (DrinkDto drink : list) {
            ImageDto image = imageMapper.selectImageByDrinkId(drink.getDrinkId());
            drink.setImage(image);
        }
        return list;
    }

    //검색 총 갯수
    public int searchDrinkCount(DrinkSearchDto drinkSearchDto) {
        return drinkMapper.searchDrinkCount(drinkSearchDto);
    }

    //필터(카테고리, 도수, 가격)
    public List<DrinkDto> getFilterList(DrinkSearchDto drinkSearchDto) {
        List<DrinkDto> list = drinkMapper.getFilterList(drinkSearchDto);
        for (DrinkDto drink : list) {
            ImageDto image = imageMapper.selectImageByDrinkId(drink.getDrinkId());
            drink.setImage(image);
        }
        return list;
    }

    //필터 총 갯수
    public int getFilterCount(DrinkSearchDto drinkSearchDto) {
        return drinkMapper.getFilterCount(drinkSearchDto);
    }

    //주류 디테일 이동
    public DrinkDto findByDrinkId(Integer drinkId) {

        DrinkDto drink = drinkMapper.findByDrinkId(drinkId);
        ImageDto image = imageMapper.selectImageByDrinkId(drinkId);
        drink.setImage(image);

        return drink;
    }

    //노트 아이디로 주류 정보
    public DrinkDto findDrinkByNoteId(Integer noteId){
        return drinkMapper.findDrinkByNoteId(noteId);
    }
}