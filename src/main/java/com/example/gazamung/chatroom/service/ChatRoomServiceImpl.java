package com.example.gazamung.chatroom.service;

import com.example.gazamung.chatroom.ChatRoomDto;
import com.example.gazamung.chatroom.entity.ChatRoom;
import com.example.gazamung.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;


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
}
