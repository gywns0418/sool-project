package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LikeDto {

    private Integer userId;
    private Integer drinkId;
    private Integer noteId;
    private LocalDateTime createdAt;

}