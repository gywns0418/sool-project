package com.example.sool.service;

import org.springframework.stereotype.Service;

import com.example.sool.dto.LikeDto;
import com.example.sool.mapper.LikeMapper;

@Service
public class LikeService {
    private final LikeMapper likeMapper;

    public LikeService(LikeMapper likeMapper){
        this.likeMapper = likeMapper;
    }

    public int insertLike(LikeDto likeDto){
        return likeMapper.insertLike(likeDto);
    }

    public int deleteLike(LikeDto likeDto){
        return likeMapper.deleteLike(likeDto);
    }

    public LikeDto findByUserId(Integer userId){
        return likeMapper.findByUserId(userId);
    }
}
