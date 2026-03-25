package com.example.sool.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.UserDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.UserService;

@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    private final UserService userService;

    public MyPageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/sidebar")
    public UserDto getMySidebar(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserId();

        return userService.getMyPageSidebar(userId);
    }
}