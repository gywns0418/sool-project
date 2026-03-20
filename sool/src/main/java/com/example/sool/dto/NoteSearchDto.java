package com.example.sool.dto;

import lombok.Data;

@Data
public class NoteSearchDto {
    private Integer drinkId;

    private String sort;
    
    private Integer page = 1;
    private Integer size = 5;

    public Integer getOffset() {
        return (page - 1) * size;
    }
}