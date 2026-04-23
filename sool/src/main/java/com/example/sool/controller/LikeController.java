package com.example.sool.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.sool.dto.LikeDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.LikeService;

@RestController
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    //주류 좋아요

    //주류 좋아요 확인
    @GetMapping("/drinks/{drinkId}/like")
    public Map<String, Object> existsLike(@PathVariable Integer drinkId, 
                        Authentication authentication) {

        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            result.put("liked", false);
            return result;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userDetails.getUserId());
        likeDto.setObjId(drinkId);
        likeDto.setObjType("DRINK");

        int exists = likeService.existsLike(likeDto);

        result.put("liked", exists > 0);
        return result;
    }

    //주류 좋아요 추가
    @PostMapping("/drinks/{drinkId}/like")
    public Map<String, Object> insertLike(@PathVariable Integer drinkId,
                                        Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userDetails.getUserId());
        likeDto.setObjId(drinkId);
        likeDto.setObjType("DRINK");

        if (likeService.existsLike(likeDto) == 0) {
            likeService.insertLike(likeDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        return result;
    }

    //주류 좋아요 삭제
    @DeleteMapping("/drinks/{drinkId}/like")
    public Map<String, Object> deleteLike(@PathVariable Integer drinkId,
                                            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userDetails.getUserId());
        likeDto.setObjId(drinkId);
        likeDto.setObjType("DRINK");

        likeService.deleteLike(likeDto);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        return result;
    }

    //테이스팅 노트 좋아요

    //노트 좋아요 확인
    @GetMapping("/notes/{noteId}/like")
    public Map<String, Object> existsNoteLike(@PathVariable Integer noteId,
                                        Authentication authentication) {

        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            result.put("liked", false);
            return result;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userDetails.getUserId());
        likeDto.setObjId(noteId);
        likeDto.setObjType("NOTE");

        int exists = likeService.existsLike(likeDto);

        result.put("liked", exists > 0);
        return result;
    }

    //노트 좋아요 추가
    @PostMapping("/notes/{noteId}/like")
    public Map<String, Object> insertNoteLike(@PathVariable Integer noteId,
                                    Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userDetails.getUserId());
        likeDto.setObjId(noteId);
        likeDto.setObjType("NOTE");

        if (likeService.existsLike(likeDto) == 0) {
            likeService.insertLike(likeDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        return result;
    }

    //노트 좋아요 삭제
    @DeleteMapping("/notes/{noteId}/like")
    public Map<String, Object> deleteNoteLike(@PathVariable Integer noteId,
                                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LikeDto likeDto = new LikeDto();
        likeDto.setUserId(userDetails.getUserId());
        likeDto.setObjId(noteId);
        likeDto.setObjType("NOTE");

        likeService.deleteLike(likeDto);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        return result;
    }

    //마이페이지 좋아요 삭제
    @PostMapping("/likes/delete")
    public ResponseEntity<?> deleteLike(@RequestBody LikeDto dto, Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        dto.setUserId(userId);

        likeService.deleteLike(dto);

        return ResponseEntity.ok().build();
    }
}