package com.example.gazamung.message;


import com.example.gazamung.chatroom.entity.ChatRoom;
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
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_SEQ")
    @SequenceGenerator(name = "MESSAGE_SEQ", sequenceName = "message_sequence", allocationSize = 1)
    private Long id;

    private String content;

    private String memberId;


    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;





}
