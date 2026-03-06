package com.example.sool.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class drinkDto {

    private Integer drinkId;
    private String categoryCode;
    private String drinkName;
    private BigDecimal abv;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}