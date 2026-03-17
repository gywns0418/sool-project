package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommonCodeDto {

    private Integer codeId;
    
    private String groupCode;
    private String code;
    private String codeName;
    private String parentCode;

    private Integer sortSeq;
    private String useYn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;

    private Integer drinkCount;  //카테고리 별 술 계산
}