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
import com.example.sool.mapper.DrinkMapper;
import com.example.sool.mapper.ImageMapper;
import com.example.sool.mapper.LikeMapper;
import com.example.sool.mapper.TastingNoteMapper;

@Service
public class LikeService {
    private final LikeMapper likeMapper;
    private final ImageMapper imageMapper;
    private final DrinkMapper drinkMapper;
    private final TastingNoteMapper tastingNoteMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeService(LikeMapper likeMapper, ImageMapper imageMapper, DrinkMapper drinkMapper,
                        TastingNoteMapper tastingNoteMapper,RedisTemplate<String, Object> redisTemplate) {
        this.likeMapper = likeMapper;
        this.imageMapper = imageMapper;
        this.drinkMapper = drinkMapper;
        this.tastingNoteMapper = tastingNoteMapper;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public int insertLike(LikeDto dto) {
        validateLikeTarget(dto);

        if (likeMapper.existsLike(dto) > 0) {
            throw new IllegalArgumentException("이미 좋아요한 항목입니다.");
        }

        if ("DRINK".equals(dto.getObjType())) {
            redisTemplate.delete("drink:top4");
        }

        return likeMapper.insertLike(dto);
    }

    public int existsLike(LikeDto dto) {
        validateLikeRequest(dto);
        return likeMapper.existsLike(dto);
    }

    @Transactional
    public int deleteLike(LikeDto dto) {
        validateLikeRequest(dto);

        if (likeMapper.existsLike(dto) == 0) {
            throw new IllegalArgumentException("좋아요 정보가 존재하지 않습니다.");
        }

        if ("DRINK".equals(dto.getObjType())) {
            redisTemplate.delete("drink:top4");
        }

        return likeMapper.deleteLike(dto);
    }

    private void validateLikeRequest(LikeDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("좋아요 요청 정보가 없습니다.");
        }

        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        if (dto.getObjType() == null || dto.getObjType().trim().isEmpty()) {
            throw new IllegalArgumentException("좋아요 대상 타입이 없습니다.");
        }

        if (!"NOTE".equals(dto.getObjType()) && !"DRINK".equals(dto.getObjType())) {
            throw new IllegalArgumentException("좋아요 대상 타입이 올바르지 않습니다.");
        }

        if (dto.getObjId() == null || dto.getObjId() <= 0) {
            throw new IllegalArgumentException("좋아요 대상 번호가 올바르지 않습니다.");
        }
    }

    private void validateLikeTarget(LikeDto dto) {
        validateLikeRequest(dto);

        if ("DRINK".equals(dto.getObjType())) {
            DrinkDto drink = drinkMapper.findByDrinkId(dto.getObjId());
            if (drink == null || "Y".equals(drink.getIsDeleted())) {
                throw new IllegalArgumentException("존재하지 않는 주류입니다.");
            }
            return;
        }

        if ("NOTE".equals(dto.getObjType())) {
            TastingNoteDto note = tastingNoteMapper.findByNoteId(dto.getObjId());
            if (note == null || "Y".equals(note.getIsDeleted())) {
                throw new IllegalArgumentException("존재하지 않는 테이스팅 노트입니다.");
            }
        }
    }

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