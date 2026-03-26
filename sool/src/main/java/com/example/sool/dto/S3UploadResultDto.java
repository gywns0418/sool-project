package com.example.sool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3UploadResultDto {
    private String fileKey;
    private String fileUrl;
}