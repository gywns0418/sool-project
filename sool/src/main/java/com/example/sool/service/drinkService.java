package com.example.sool.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
import com.example.sool.dto.ImageDto;
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

    //주류 목록 조회 (검색 + 필터 + 페이지 처리)
    public Map<String, Object> getDrinkList(DrinkSearchDto dto) {

        List<DrinkDto> list;
        int totalCount;

        //검색어 존재 여부 확인
        boolean hasKeyword = dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty();

        if (hasKeyword) {
            //검색어 기반 조회 ( 주류 한글, 영문 이름, 카테고리 대,소분류 )
            list = drinkMapper.searchDrinkList(dto);
            totalCount = drinkMapper.searchDrinkCount(dto);
        } else {
            //필터 기반 조회 (카테고리, 도수, 가격)
            list = drinkMapper.getFilterList(dto);
            totalCount = drinkMapper.getFilterCount(dto);
        }

        //주류별 대표 이미지 조회 후 세팅
        for (DrinkDto drink : list) {
            ImageDto image = imageMapper.selectImageByDrinkId(drink.getDrinkId());
            drink.setImage(image);
        }

        //총 페이지 계산
        int totalPage = (int) Math.ceil((double) totalCount / dto.getSize());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("page", dto.getPage());
        result.put("size", dto.getSize());
        result.put("totalPage", totalPage);

        return result;
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