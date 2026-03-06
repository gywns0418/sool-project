package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class commentDto {

    private Integer commentId;
    private Integer noteId;
    private Integer userId;
    private Integer parentCommentId;
    private String content;
    private String isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}