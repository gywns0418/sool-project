package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ImageDto {

    private Integer imageId;

    private Integer objId;
    private String objType;
    
    private String fileKey;
    private String fileUrl;
    
    private LocalDateTime createdAt;

}
