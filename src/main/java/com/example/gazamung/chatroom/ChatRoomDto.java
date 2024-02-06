package com.example.gazamung.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ChatRoomDto {

    private String memberIdx;
    private String roomName;
}
