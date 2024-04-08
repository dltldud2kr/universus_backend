package com.example.gazamung.chatRoom;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMessageService {

    ChatMessage saveChatMessage(int chatRoomType, String chatRoomId, Long memberIdx, String content, String nickname);


    List<ChatMessage> chatList(String chatRoomId);


}
