package com.example.gazamung.chatroom;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.chatroom.entity.ChatRoom;
import com.example.gazamung.chatroom.service.ChatRoomService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.dto.TokenDto;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.message.Message;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "채팅방 API", description = "")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chat")
    public String chatGET(){

        log.info("@ChatController, chat GET()");

        return "chat";
    }

    @PostMapping("/chat/create")
    public String createChatRoom(ChatRoomDto chatRoomDto) {

        chatRoomService.create(chatRoomDto);

        return null;

    }
}
