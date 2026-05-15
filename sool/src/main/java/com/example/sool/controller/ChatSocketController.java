package com.example.sool.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.sool.dto.ChatMessageDto;
import com.example.sool.service.ChatService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessageDto dto) {
        chatService.insertChatMessage(dto);

        ChatMessageDto savedMessage = chatService.getChatMessage(dto.getMessageId());

        messagingTemplate.convertAndSend(
            "/topic/chat/room/" + dto.getRoomId(),
            savedMessage
        );
    }
}