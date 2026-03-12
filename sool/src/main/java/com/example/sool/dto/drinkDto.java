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

    private BigDecimal avgRating;
    private Integer noteCount;
    private Integer likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String isDeleted;
    private LocalDateTime deletedAt;


}