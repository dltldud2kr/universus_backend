package com.example.gazamung.chat.chatRoom;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatMessage.ChatMessageRepository;
import com.example.gazamung.chat.chatMessage.ChatMessageService;
import com.example.gazamung.chat.dto.DirectMessageReq;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.mapper.ChatMapper;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
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
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMapper chatMapper;


    @Override
    public boolean directMessage(DirectMessageReq dto) {

        Member sender = memberRepository.findById(dto.getSenderIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        Member receiver = memberRepository.findById(dto.getReceiverIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));


        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(2)
                .build();
        chatRoomRepository.save(chatRoom);

        ChatMember senderChatMember = ChatMember.builder()
                .chatRoomType(2)
                .chatRoomId(chatRoom.getChatRoomId())
                .memberIdx(dto.getSenderIdx())
                .customChatRoomName(receiver.getNickname())
                .build();

        ChatMember receiverChatMember = ChatMember.builder()
                .chatRoomType(2)
                .chatRoomId(chatRoom.getChatRoomId())
                .memberIdx(dto.getReceiverIdx())
                .customChatRoomName(sender.getNickname())
                .build();

        chatMemberRepository.save(senderChatMember);
        chatMemberRepository.save(receiverChatMember);

        return true;
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
