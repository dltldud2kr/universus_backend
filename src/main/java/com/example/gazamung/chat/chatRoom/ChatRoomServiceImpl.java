package com.example.gazamung.chat.chatRoom;

import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatMessage.ChatMessageRepository;
import com.example.gazamung.chat.chatMessage.ChatMessageService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMapper chatMapper;


    @Override
    public List<ChatRoom> chatRoomList() {

        return null;
    }

    @Override
    public Map<String, Object> myChatRoomList(Long memberIdx) {

        // 응답용 Map 생성 및 값 추가
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> chatRoomList = new ArrayList<>();

        List<ChatMember> chatMembers = chatMemberRepository.findAllByMemberIdx(memberIdx);

        for (ChatMember chatMember : chatMembers) {
            Map<String, Object> chatRoomInfo = new HashMap<>();
            long chatRoomId = chatMember.getChatRoomId();
            String recentChat = chatMapper.findLatestMessageContentByChatRoomId(chatRoomId);

            chatRoomInfo.put("chatRoom", chatMember);
            chatRoomInfo.put("recentChat", recentChat);
            chatRoomList.add(chatRoomInfo);
        }

        response.put("chatRooms", chatRoomList);
        return response;
    }
}
