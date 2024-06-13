package com.example.gazamung.chat.chatRoom;

import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatMember.ChatMemberRepository;
import com.example.gazamung.chat.chatMessage.ChatMessage;
import com.example.gazamung.chat.chatMessage.ChatMessageRepository;
import com.example.gazamung.chat.dto.DirectMessageReq;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.mapper.ChatMapper;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.sql.TIMESTAMP;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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


    /**
     * 1 대 1 채팅
     * @param dto
     * @return
     */
    @Override
    public Map<String, Object> directMessage(DirectMessageReq dto) {

        long chatRoomId = chatMapper.countChatRoomsByMembersAndType(dto.getSenderIdx(), dto.getReceiverIdx(), 2); // 0: 1:1 채팅

        // 이미 1:1 채팅방이 존재함
        if (chatRoomId != -1) {
            Map<String, Object> existingData = new HashMap<>();

            existingData.put("chatRoomId", chatRoomId);

            ChatMember chatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(dto.getSenderIdx(), chatRoomId);
            existingData.put("customChatRoomName", chatMember.getCustomChatRoomName());


            throw new CustomException(CustomExceptionCode.ALREADY_EXIST_CHATROOM, existingData);
        }

        Member sender = memberRepository.findById(dto.getSenderIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        Member receiver = memberRepository.findById(dto.getReceiverIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));


        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomType(2)
                .build();
        chatRoomRepository.save(chatRoom);

        // 상대방의 닉네임 및 프로필사진으로 채팅방명과 사진을 저장.
        ChatMember senderChatMember = ChatMember.builder()
                .chatRoomType(2)
                .chatRoomId(chatRoom.getChatRoomId())
                .memberIdx(dto.getSenderIdx())
                .customChatRoomName(receiver.getNickname())
                .chatRoomImg(receiver.getProfileImgUrl())
                .build();

        ChatMember receiverChatMember = ChatMember.builder()
                .chatRoomType(2)
                .chatRoomId(chatRoom.getChatRoomId())
                .memberIdx(dto.getReceiverIdx())
                .customChatRoomName(sender.getNickname())
                .chatRoomImg(sender.getProfileImgUrl())
                .build();

        chatMemberRepository.save(senderChatMember);
        chatMemberRepository.save(receiverChatMember);

        ChatMember chatMember = chatMemberRepository.findByMemberIdxAndChatRoomId(dto.getSenderIdx(),chatRoom.getChatRoomId());

        Map<String, Object> response = new HashMap<>();
        response.put("chatRoomId", chatRoom.getChatRoomId());
        response.put("customChatRoomName", chatMember.getCustomChatRoomName());
        return response;
    }



    @Override
    @Transactional
    public Map<String, Object> myChatRoomList(Long memberIdx) {

        // List 데이터를 Map 에 넣어서 반환.
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> chatRoomList = new ArrayList<>();

        List<ChatMember> chatMembers = chatMemberRepository.findAllByMemberIdx(memberIdx);

        // Mapper 를 이용해 최근 채팅 메세지와 시간을 가져옴.
        for (ChatMember chatMember : chatMembers) {
            Map<String, Object> chatRoomInfo = new HashMap<>();
            long chatRoomId = chatMember.getChatRoomId();
            Map<String, Object> latestMessage = chatMapper.findLatestMessageByChatRoomId(chatRoomId);

            chatRoomInfo.put("chatRoom", chatMember);
            if (latestMessage != null) {
                Object regDtObject = latestMessage.get("REG_DT");

                LocalDateTime regDt = null;
                if (regDtObject instanceof TIMESTAMP) {

                    // oracle.sql.TIMESTAMP를 java.sql.Timestamp로 변환
                    TIMESTAMP oracleTimestamp = (TIMESTAMP) regDtObject;
                    Timestamp sqlTimestamp = null;
                    try {
                        sqlTimestamp = oracleTimestamp.timestampValue();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    // java.sql.Timestamp를 LocalDateTime으로 변환
                    regDt = sqlTimestamp.toLocalDateTime();
                } else {
                }

                chatRoomInfo.put("recentChat", latestMessage.get("CONTENT"));
                chatRoomInfo.put("recentChatDate", regDt);
            } else {
                chatRoomInfo.put("recentChat", "No messages");
                chatRoomInfo.put("recentChatDate", null);
            }
            chatRoomList.add(chatRoomInfo);
        }
        response.put("chatRooms", chatRoomList);
        return response;
    }



    @Override
    @Transactional
    public boolean deleteChatRoom(Long chatRoomId, Long memberIdx) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new CustomException(CustomExceptionCode.NOT_FOUND_CHATROOM));

        Member member = memberRepository.findById(memberIdx).orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
        chatMemberRepository.deleteByChatRoomIdAndMemberIdx(chatRoomId, memberIdx);

        ChatMessage chatMessage =  ChatMessage.builder()
                .chatRoomType(chatRoom.getChatRoomType())
                .chatRoomId(chatRoom.getChatRoomId())
                .content(member.getNickname() + "님이 퇴장하셨습니다.")
                .nickname(" ")
                .regDt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        return true;
    }



}
