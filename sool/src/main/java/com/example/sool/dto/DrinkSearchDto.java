package com.example.sool.dto;

import lombok.Data;

@Data
public class DrinkSearchDto {
    private Integer userId;

    private String keyword;

    private String categoryCode;

    private Integer abvLow;
    private Integer abvHigh;
    
    private Integer priceLow;
    private Integer priceHigh;

    private String sort;
    
    private Integer page = 1;
    private Integer size = 12;

    public Integer getOffset() {
        return (page - 1) * size;
    }
}