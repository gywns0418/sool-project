package com.example.sool.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.ChatMessageDto;
import com.example.sool.dto.ChatRoomDto;
import com.example.sool.dto.ChatRoomUserDto;

@Mapper
public interface ChatMapper {

    ChatMessageDto selectChatMessageList(int roomId);
    
    ChatRoomDto selectChatRoomList(int roomId);
    
    int insertChatMessage(ChatMessageDto dto);
    
    int insertChatRoomUser(ChatRoomUserDto dto);
    
    int insertChatRoom(ChatRoomDto dto);
}
