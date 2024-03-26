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
public class ChatMember {

    @Id
    private Long memberIdx;

    private Long chatRoomId;  // 대항전 id
    
}
