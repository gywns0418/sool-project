package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LikeDto {

    private Integer userId;

    private Integer objId;
    private String objType;

    private LocalDateTime createdAt;

}