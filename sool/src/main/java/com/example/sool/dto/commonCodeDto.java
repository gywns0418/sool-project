package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class commonCodeDto {

    private Integer codeId;
    private String groupCode;
    private String code;
    private String codeName;
    private String parentCode;
    private Integer sortSeq;
    private String useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}