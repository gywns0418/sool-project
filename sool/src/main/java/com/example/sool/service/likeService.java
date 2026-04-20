package com.example.sool.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.ImageDto;
import com.example.sool.dto.LikeDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.mapper.ImageMapper;
import com.example.sool.mapper.LikeMapper;

@Service
public class LikeService {
    private final LikeMapper likeMapper;
    private final ImageMapper imageMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeService(LikeMapper likeMapper, ImageMapper imageMapper, RedisTemplate<String, Object> redisTemplate){
        this.likeMapper = likeMapper;
        this.imageMapper = imageMapper;
        this.redisTemplate = redisTemplate;
    }

    public int insertLike(LikeDto likeDto){
        //좋아요 추가 시 주류TOP4 캐시 삭제
        redisTemplate.delete("drink:top4");
        
        return likeMapper.insertLike(likeDto);
    }

    public int existsLike(LikeDto likeDto){
        return likeMapper.existsLike(likeDto);
    }

    @Transactional
    public int deleteLike(LikeDto dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        if (dto.getObjType() == null || dto.getObjType().trim().isEmpty()) {
            throw new IllegalArgumentException("좋아요 대상 타입이 없습니다.");
        }

        if (dto.getObjId() == null || dto.getObjId() <= 0) {
            throw new IllegalArgumentException("좋아요 대상 번호가 올바르지 않습니다.");
        }

        if (likeMapper.existsLike(dto) == 0) {
            throw new IllegalArgumentException("좋아요 정보가 존재하지 않습니다.");
        }

        //좋아요 삭제 시 주류TOP4 캐시 삭제
        redisTemplate.delete("drink:top4");

        return likeMapper.deleteLike(dto);
    }

    //마이페이지 좋아요 정보
    public Map<String, Object> getMyLikes(int userId) {
        
        List<TastingNoteDto> likedNoteList = likeMapper.findLikedNoteList(userId);
        for (TastingNoteDto note : likedNoteList) {
            ImageDto image = new ImageDto();
            image.setObjId(note.getNoteId());
            image.setObjType("NOTE");

            image = imageMapper.selectImage(image);
            note.setImage(image);
        }

        List<DrinkDto> likedDrinkList = likeMapper.findLikedDrinkList(userId);
        for (DrinkDto drink : likedDrinkList) {
            ImageDto image = new ImageDto();
            image.setObjId(drink.getDrinkId());
            image.setObjType("DRINK");

            image = imageMapper.selectImage(image);
            drink.setImage(image);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("likedNoteList", likedNoteList);
        result.put("likedDrinkList", likedDrinkList);

        return result;
    }


}
