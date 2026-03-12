package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserDto {

    private Integer userId;

    private String loginId;
    private String password;
    private String name;
    private String email;

    private String roleCode;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;
    private LocalDateTime deletedAt;

}