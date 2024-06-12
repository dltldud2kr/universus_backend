package com.example.gazamung.fcmSend;

import com.example.gazamung.exception.ApiResponseWrapper;
import com.example.gazamung.fcmSend.FcmSendDto;
import com.example.gazamung.fcmSend.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

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
