package com.example.sool.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.ChatMessageDto;
import com.example.sool.dto.ChatRoomDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("")
    public List<ChatRoomDto> getChatRoomList(Authentication authentication){

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("list"+chatService.getChatRoomList(userDetails.getUserId()));
        
        return chatService.getChatRoomList(userDetails.getUserId());
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageDto> getChatMessageList(@PathVariable int roomId){
        return chatService.getChatMessageList(roomId);
    }
}
