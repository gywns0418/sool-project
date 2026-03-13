package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDto {

    private Integer commentId;
    private Integer noteId;
    private Integer userId;
    private Integer parentCommentId;

    private Integer groupId;
    private Integer depth;

    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;
    private LocalDateTime deletedAt;

    private Integer drinkCount;  //z카테고리 별 술 계산

}