package com.example.gazamung.chat;


import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomService;
import com.example.gazamung.dto.ResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "모임 API", description = "")
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chatList")
    public ResultDTO chatList(@RequestParam Long chatRoomId) {

        return null;
    }

    @GetMapping("/myChatList")
    public List<ChatMember> myChatList(@RequestParam Long memberIdx) {

        List<ChatMember> chatRoomList = chatRoomService.myChatRoomList(memberIdx);
        return chatRoomList;
    }



}
