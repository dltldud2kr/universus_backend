package com.example.gazamung.chatroom.entity;


import com.example.gazamung.message.Message;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

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
    private Long roomId;

    private String roomName;

    private String memberIdx;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages;




}
