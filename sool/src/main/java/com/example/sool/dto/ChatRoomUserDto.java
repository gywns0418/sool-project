package com.example.sool.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoomUserDto {
    private Integer roomId;
    private Integer userId;
    private Integer lastReadMessageId;
    private LocalDateTime enteredAt;
    private LocalDateTime leftAt;
}
