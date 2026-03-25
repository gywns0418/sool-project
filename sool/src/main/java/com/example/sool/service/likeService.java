package com.example.sool.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.sool.dto.LikeDto;
import com.example.sool.mapper.LikeMapper;

@Service
public class LikeService {
    private final LikeMapper likeMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public LikeService(LikeMapper likeMapper, RedisTemplate<String, Object> redisTemplate){
        this.likeMapper = likeMapper;
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

    public int deleteLike(LikeDto likeDto){
        //좋아요 삭제 시 주류TOP4 캐시 삭제
        redisTemplate.delete("drink:top4");
        
        return likeMapper.deleteLike(likeDto);
    }

    public List<LikeDto> findByUserId(Integer userId){
        return likeMapper.findByUserId(userId);
    }
}
