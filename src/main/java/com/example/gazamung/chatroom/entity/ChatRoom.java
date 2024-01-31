package com.example.gazamung.chatroom.entity;


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
    private Long roomIdx;

    private String roomName;

    private String memberId;





}
