package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CodeGroupDto {

    private String groupCode;
    private String groupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
