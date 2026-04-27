package com.example.sool.dto;
import lombok.Data;

@Data
public class TastingNoteMetricDto {
    
    private Integer noteId;
    private String metricCode;
    private Integer score;

    private String metricName;
    private String codeDesc;
    private Double avgScore;
}