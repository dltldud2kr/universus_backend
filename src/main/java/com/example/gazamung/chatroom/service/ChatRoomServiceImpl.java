package com.example.gazamung.chatroom.service;

import com.example.gazamung.chatroom.ChatRoomDto;
import com.example.gazamung.chatroom.entity.ChatRoom;
import com.example.gazamung.chatroom.repository.ChatRoomRepository;
import com.example.gazamung.message.Message;
import com.example.gazamung.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;


    @Override
    public boolean create(ChatRoomDto dto) {

        System.out.println(dto.getMemberIdx());
        System.out.println(dto.getRoomName());

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(dto.getRoomName())
                .memberIdx(dto.getMemberIdx())
                .build();

        chatRoomRepository.save(chatRoom);

        return true;
    }

    @Override
    public Message saveMessage(Message message) {
        // 메시지 저장 로직
        return messageRepository.save(message);
    }
}
