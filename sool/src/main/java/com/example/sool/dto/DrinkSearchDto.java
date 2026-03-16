package com.example.sool.dto;

import lombok.Data;

@Data
public class DrinkSearchDto {

    private String keyword;

    private String categoryCode;
    private String typeCode;

    private Integer page = 1;
    private Integer size = 12;

    public Integer getOffset() {
        return (page - 1) * size;
    }
}