package com.example.gazamung.chat.chatRoom;

import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.dto.DirectMessageReq;
import com.example.gazamung.dto.ResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ChatRoomService {

    Map<String, Object> directMessage(DirectMessageReq dto);

    /**
     * 나의 채팅방 리스트
     * @param memberIdx
     * @return
     */
    Map<String, Object> myChatRoomList(Long memberIdx);

    boolean deleteChatRoom( Long chatRoomId, Long memberIdx);

}
