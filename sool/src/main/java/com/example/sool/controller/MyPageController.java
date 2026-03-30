package com.example.sool.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.LikeDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.dto.UserDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.LikeService;
import com.example.sool.service.TastingNoteService;
import com.example.sool.service.UserService;

@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    private final UserService userService;
    private final LikeService likeService;
    private final TastingNoteService tastingNoteService;

    public MyPageController(UserService userService, LikeService likeService, 
                            TastingNoteService tastingNoteService) {
        this.userService = userService;
        this.likeService = likeService;
        this.tastingNoteService = tastingNoteService;
    }

    @GetMapping("/sidebar")
    public UserDto getMySidebar(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        return userService.getMyPageSidebar(userId);
    }

    @GetMapping("/myLikes")
    public Map<String, Object> getMyLikes(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        return likeService.getMyLikes(userId);
    }

    @GetMapping("/myTastingNote")
    public Map<String, Object> getMyTastingNote(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        List<TastingNoteDto> noteList = tastingNoteService.findByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("noteList", noteList);
        return result;
    }

    //마이페이지 비밀번호 재설정
    @PostMapping("/password")
    public ResponseEntity<?> updateMyPassword(@RequestBody Map<String, String> param, Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String currentPassword = param.get("currentPassword");
        String newPassword = param.get("newPassword");

        userService.updateMyPassword(userId, currentPassword, newPassword);

        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }
}