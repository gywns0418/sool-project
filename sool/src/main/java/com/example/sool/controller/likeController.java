package com.example.sool.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.sool.dto.LikeDto;
import com.example.sool.dto.UserDto;
import com.example.sool.service.LikeService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/drinks")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    //좋아요 존재 확인
    @GetMapping("/{drinkId}/like")
    public Map<String, Object> existsLike(
            @PathVariable Integer drinkId,
            HttpSession session
    ) {
        Map<String, Object> result = new HashMap<>();

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            result.put("liked", false);
            return result;
        }

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(loginUser.getUserId());
        likeDto.setObjId(drinkId);
        likeDto.setObjType("DRINK");

        int exists = likeService.existsLike(likeDto);

        result.put("liked", exists > 0);
        return result;
    }

    //좋아요 추가
    @PostMapping("/{drinkId}/like")
    public Map<String, Object> insertLike(
            @PathVariable Integer drinkId,
            HttpSession session
    ) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(loginUser.getUserId());
        likeDto.setObjId(drinkId);
        likeDto.setObjType("DRINK");

        if (likeService.existsLike(likeDto) == 0) {
            likeService.insertLike(likeDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        return result;
    }

    //좋아요 삭제
    @DeleteMapping("/{drinkId}/like")
    public Map<String, Object> deleteLike(
            @PathVariable Integer drinkId,
            HttpSession session
    ) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(loginUser.getUserId());
        likeDto.setObjId(drinkId);
        likeDto.setObjType("DRINK");

        likeService.deleteLike(likeDto);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        return result;
    }
}