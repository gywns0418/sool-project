package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatMessageDto {
    private Integer messageId;
    private Integer roomId;
    private Integer senderId;
    private String message;
    private LocalDateTime createdAt;
    private String isDeleted;

    private String senderName;
}
