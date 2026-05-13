package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.ChatMessageDto;
import com.example.sool.dto.ChatRoomDto;
import com.example.sool.dto.ChatRoomUserDto;

@Mapper
public interface ChatMapper {

    List<ChatMessageDto> selectChatMessageList(int roomId);
    
    List<ChatRoomDto> selectChatRoomList(int userId);
    
    ChatMessageDto selectChatMessage(int messageId);
    
    int insertChatMessage(ChatMessageDto dto);
    
    int insertChatRoomUser(ChatRoomUserDto dto);
    
    int insertChatRoom(ChatRoomDto dto);
}
