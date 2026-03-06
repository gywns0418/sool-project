package com.example.sool.dto;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class tastingNoteDto {

    private Integer noteId;
    private Integer userId;
    private Integer drinkId;
    private String title;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;
    private LocalDateTime deletedAt;

}