package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoomDto {
    private Integer roomId;
    private LocalDateTime createdAt;
}
