package com.example.gazamung.ChatRoom;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_MESSAGE_SEQ")
    @SequenceGenerator(name = "CHAT_MESSAGE_SEQ", sequenceName = "chat_message_sequence", allocationSize = 1)
    private Long chatMessageId;

    private int chatRoomType;
    private Long chatRoomId;        // 채팅방 ID
    private Long memberIdx;         // 발신자 ID
    private String content;         // 메세지 내용
    private LocalDateTime regDt;    // 발신 시간
    
}
