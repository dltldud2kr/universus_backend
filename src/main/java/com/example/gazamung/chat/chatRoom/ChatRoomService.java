package com.example.gazamung.chat.chatRoom;

import com.example.gazamung.chat.chatMember.ChatMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ChatRoomService {
    /**
     * 모든 채팅방 리스트
     * @return
     */
    List<ChatRoom> chatRoomList();

    /**
     * 나의 채팅방 리스트
     * @param memberIdx
     * @return
     */
    Map<String, Object> myChatRoomList(Long memberIdx);
}
