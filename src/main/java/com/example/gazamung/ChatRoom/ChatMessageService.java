package com.example.gazamung.ChatRoom;

import org.springframework.stereotype.Service;

@Service
public interface ChatMessageService {

    void saveChatMessage(String chatRoomId, Long memberIdx, String content);
}
