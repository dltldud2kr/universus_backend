package com.example.gazamung.chat.chatMessage;

import com.example.gazamung.chat.chatMessage.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMessageService {

    ChatMessage saveChatMessage(int chatRoomType, String chatRoomId, Long memberIdx, String content, String nickname);


    List<ChatMessage> chatList(String chatRoomId);


}
