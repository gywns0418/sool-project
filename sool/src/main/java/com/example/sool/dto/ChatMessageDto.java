package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Integer messageId;
    private Integer roomId;
    private Integer userId;
    private String senderName;
    private String message;
    private LocalDateTime createdAt;
}
