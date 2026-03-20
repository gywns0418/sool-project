package com.example.sool.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DrinkDto {

    private Integer drinkId;
    private String categoryCode;
    private String typeCode;

    private String drinkName;
    private String drinkNameEn;

    private BigDecimal abv;
    private Integer price;
    private String country;
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;

    //추가

    
    private Integer likeCount; //좋아요 수
    private boolean liked; //좋아요 표시
    private Integer noteCount; //노트 수
    private Double avgRating; //평균 별점

    private String categoryName;
    private String typeName;   

}