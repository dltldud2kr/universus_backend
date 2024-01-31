package com.example.gazamung.webSocket;

import com.example.gazamung.chatroom.entity.ChatRoom;
import com.example.gazamung.chatroom.repository.ChatRoomRepository;
import com.example.gazamung.message.Message;
import com.example.gazamung.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {


    private final SimpMessagingTemplate messagingTemplate;

    private final ChatRoomRepository chatRoomRepository;

    private final MessageRepository messageRepository;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, Message message) {
        // Save the message to the database
        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        message.setChatRoom(chatRoom);
        messageRepository.save(message);    // 이 부분이 빨간줄

        // Broadcast the message to all subscribers
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }
}
