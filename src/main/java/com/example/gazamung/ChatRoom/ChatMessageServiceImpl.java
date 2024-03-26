package com.example.gazamung.ChatRoom;


import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void saveChatMessage(String chatRoomId, Long memberIdx, String content) {
        System.out.println("chatroomID : " + chatRoomId);
        Long chatId = Long.valueOf(chatRoomId);
        chatRoomRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));


        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(chatId)
                .memberIdx(memberIdx)
                .content(content)
                .regDt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);


    }
}
