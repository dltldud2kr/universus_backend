package com.example.gazamung.ChatRoom;


import com.example.gazamung._enum.CustomExceptionCode;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.member.entity.Member;
import com.example.gazamung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Override
    public ChatMessage saveChatMessage(int chatRoomType, String chatRoomId, Long memberIdx, String content, String nickname) {
        System.out.println("chatroomID : " + chatRoomId);
        Long chatId = Long.valueOf(chatRoomId);


        // RoomType 과 RoomId를 체크
        chatRoomRepository.findByChatRoomIdAndChatRoomType(chatId, chatRoomType)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BATTLE));


        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomType(chatRoomType)
                .chatRoomId(chatId)
                .memberIdx(memberIdx)
                .nickname(nickname)
                .content(content)
                .regDt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        return chatMessage;
    }

    @Override
    public List<ChatMessage> chatList(String chatRoomId) {
        Long chatId = Long.valueOf(chatRoomId);

        return chatMessageRepository.findAllByChatRoomIdOrderByRegDtDesc(chatId);
    }



}
