package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReportDto {

    private Integer reportId;
    private Integer userId;

    private Integer objId;
    private String objType;

    private String reason;
    private String statusCode;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;
    private LocalDateTime deletedAt;

}