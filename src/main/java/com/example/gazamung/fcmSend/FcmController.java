package com.example.gazamung.fcmSend;

import com.example.gazamung.exception.ApiResponseWrapper;
import com.example.gazamung.fcmSend.FcmSendDto;
import com.example.gazamung.fcmSend.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "FCM API", description = "")
public class FcmController {

    private final FcmService fcmService;


    @Operation(summary = "FCM 전송 기능 ", description = "" +
            " 회원이 참가한 채팅 리스트를 보여줍니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseWrapper<Integer>> sendFcm(@RequestBody FcmSendDto fcmSendDto) {
        try {
            int result = fcmService.sendMessageTo(fcmSendDto);
            ApiResponseWrapper<Integer> response = ApiResponseWrapper.<Integer>builder()
                    .resultCode(HttpStatus.OK.value())
                    .resultMsg("FCM 메시지 전송 성공")
                    .result(result)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponseWrapper<Integer> response = ApiResponseWrapper.<Integer>builder()
                    .resultCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .resultMsg("FCM 메시지 전송 실패: " + e.getMessage())
                    .result(0)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
