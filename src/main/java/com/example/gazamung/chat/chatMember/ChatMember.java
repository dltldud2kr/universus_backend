package com.example.gazamung.chat.chatMember;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_MEMBER_SEQ")
    @SequenceGenerator(name = "CHAT_MEMBER_SEQ", sequenceName = "chat_member_sequence", allocationSize = 1)
    private Long idx;

    private Long memberIdx;

    private int chatRoomType;
    private String chatRoomName;


    private Long chatRoomId;  // 대항전 id
    
}
