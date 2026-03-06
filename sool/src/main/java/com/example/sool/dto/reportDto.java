package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class reportDto {

    private Integer reportId;
    private Integer reporterUserId;
    private Integer noteId;
    private Integer commentId;
    private String reason;
    private String statusCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}