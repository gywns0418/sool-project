package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class imageDto {

    private Integer imageId;
    private Integer noteId;
    private Integer drinkId;
    private String fileKey;
    private String fileUrl;
    private LocalDateTime createdAt;

}
