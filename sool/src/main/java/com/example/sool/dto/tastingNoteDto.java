package com.example.sool.dto;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TastingNoteDto {

    private Integer noteId;
    private Integer userId;
    private Integer drinkId;
    private String title;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;

    //홈 최근 테이스팅 노트 
    private String userName;
    
    private String drinkName;
    private String categoryCode;

    private String typeName;
}