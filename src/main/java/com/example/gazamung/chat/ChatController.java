package com.example.gazamung.chat;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.chat.chatMember.ChatMember;
import com.example.gazamung.chat.chatRoom.ChatRoom;
import com.example.gazamung.chat.chatRoom.ChatRoomService;
import com.example.gazamung.chat.dto.DirectMessageReq;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "모임 API", description = "")
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;


    @Operation(summary = "내 채팅방 리스트 ", description = "참가한 채팅방정보, 채팅방 마지막 대화 반환" +
            " 회원이 참가한 채팅 리스트를 보여줍니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @GetMapping("/myChatList")
    public ResultDTO<Map<String, Object>> myChatList(@RequestParam Long memberIdx) {

        try {
            Map<String, Object> result = chatRoomService.myChatRoomList(memberIdx);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "채팅방 리스트 정보", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }

    @PostMapping("/direct")
    public ResultDTO directMessage(@RequestBody DirectMessageReq dto){
        try {
            Map<String, Object> result = chatRoomService.directMessage(dto);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "1대1 채팅방 생성", result);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }

    @DeleteMapping("/delete")
    public ResultDTO deleteChatRoom( @RequestParam Long chatRoomId, @RequestParam Long memberIdx ){

        try {
            chatRoomService.deleteChatRoom(chatRoomId, memberIdx);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "채팅방 삭제", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


}
