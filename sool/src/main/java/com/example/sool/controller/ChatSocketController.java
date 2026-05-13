package com.example.sool.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.sool.dto.ChatMessageDto;
import com.example.sool.service.ChatService;

@Controller
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatSocketController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

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