package com.example.gazamung.chatroom.service;

import com.example.gazamung.chatroom.ChatRoomDto;
import com.example.gazamung.chatroom.entity.ChatRoom;
import com.example.gazamung.chatroom.repository.ChatRoomRepository;
import com.example.gazamung.config.ChatHandler;
import com.example.gazamung.message.Message;
import com.example.gazamung.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ChatHandler chatHandler;


    @Override
    public boolean create(ChatRoomDto dto) {

        System.out.println(dto.getMemberIdx());
        System.out.println(dto.getRoomName());



        // 웹소켓 세션 생성
//        WebSocketSession session = createWebSocketSession();

//        String sessionId = session.getId();



        log.info("Chat room created successfully.");
        return true;
    }



    // 웹소켓 세션을 생성하는 메서드
//    private WebSocketSession createWebSocketSession() {
//        try {
//            // ChatHandler를 통해 웹소켓 세션을 생성하여 반환
//            return chatHandler.createWebSocketSession();
//        } catch (Exception e) {
//            log.error("Failed to create WebSocket session", e);
//            return null;
//        }
//    }
}
