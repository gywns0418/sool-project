package com.example.sool.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.ChatMessageDto;
import com.example.sool.dto.ChatRoomDto;
import com.example.sool.mapper.ChatMapper;

@Service
public class ChatService {

    private final ChatMapper chatMapper;

    public ChatService(ChatMapper chatMapper){
        this.chatMapper = chatMapper;
    }

    public List<ChatRoomDto> getChatRoomList(int userId){
        
        return chatMapper.selectChatRoomList(userId);
    }

    public List<ChatMessageDto> getChatMessageList(int roomId){
        return chatMapper.selectChatMessageList(roomId);
    }

    public ChatMessageDto getChatMessage(int messageId){
        return chatMapper.selectChatMessage(messageId);
    }

    public int insertChatMessage(ChatMessageDto dto){
        return chatMapper.insertChatMessage(dto);
    }

    
}
