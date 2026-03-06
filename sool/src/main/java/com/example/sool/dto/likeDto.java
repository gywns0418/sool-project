package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class likeDto {

    private Integer likeId;
    private Integer userId;
    private Integer noteId;
    private LocalDateTime createdAt;

}