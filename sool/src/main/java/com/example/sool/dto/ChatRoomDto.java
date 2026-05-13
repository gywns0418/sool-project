package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoomDto {
    private Integer roomId;
    private String title;
    private LocalDateTime createdAt;

    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
}
