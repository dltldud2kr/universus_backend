package com.example.gazamung.ChatRoom;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMessageService {

    void saveChatMessage(int chatRoomType, String chatRoomId, Long memberIdx, String content);


    List<ChatMessage> chatList(Long chatRoomId);


}
