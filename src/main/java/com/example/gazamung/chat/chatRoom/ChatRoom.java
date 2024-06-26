package com.example.gazamung.chat.chatRoom;


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
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHATROOM_SEQ")
    @SequenceGenerator(name = "CHATROOM_SEQ", sequenceName = "chatroom_sequence", allocationSize = 1)
    private Long chatRoomId;

//    private String chatRoomName;
    private int chatRoomType;   // 0:대학  1:과   2:개인 3: 모임

    private Long dynamicId;  // 대항전 id

}
