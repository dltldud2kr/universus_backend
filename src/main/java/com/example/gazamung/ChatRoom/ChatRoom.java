package com.example.gazamung.ChatRoom;


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
    private int chatRoomType;   // 0:대학  1:과   2:개인

    private Long univBattleId;  // 대항전 id

}
