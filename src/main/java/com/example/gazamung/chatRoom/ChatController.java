package com.example.gazamung.chatRoom;


import com.example.gazamung.dto.ResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "모임 API", description = "")
@RequestMapping("/api/v1/chat")
public class ChatController {


    @GetMapping("/chatList")
    public ResultDTO chatList(@RequestParam Long chatRoomId) {

        return null;
    }



}
