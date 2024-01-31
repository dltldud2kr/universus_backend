package com.example.gazamung.chatroom;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.chatroom.service.ChatRoomService;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.dto.TokenDto;
import com.example.gazamung.exception.CustomException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "채팅방 API", description = "")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;



    @PostMapping("/chatRoom/create")
    public ResultDTO createChatRoom(ChatRoomDto dto){

        log.info("룸이름 :" + dto.getRoomName());
        log.info("멤버아이디엑스 : " + dto.getMemberIdx());
        ;

        try {
            boolean result = chatRoomService.create(dto);
            return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(), "채팅방 생성 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }
}
