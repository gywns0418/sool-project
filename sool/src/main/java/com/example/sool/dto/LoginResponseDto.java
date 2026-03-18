package com.example.sool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private Integer userId;
    private String loginId;
    private String name;
    private String roleCode;
}