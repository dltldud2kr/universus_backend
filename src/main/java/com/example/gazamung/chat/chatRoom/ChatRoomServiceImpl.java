package com.example.gazamung.chat.chatRoom;

import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatMessage.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatMemberRepository chatMemberRepository;


    @Override
    public List<ChatRoom> chatRoomList() {

        return null;
    }

    @Override
    public List<ChatMember> myChatRoomList(Long memberIdx) {
            List<ChatMember> chatMember = chatMemberRepository.findAllByMemberIdx(memberIdx);
        return chatMember;
    }
}
