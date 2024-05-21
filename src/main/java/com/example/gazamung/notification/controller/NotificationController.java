package com.example.gazamung.notification.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.dto.NotifyRes;
import com.example.gazamung.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notify")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
@Tag(name = "알림 API", description = "")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 전송", description = "" +
            "알림 전송 TEST API입니다.." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 요청 성공"),
    })
    @PostMapping("/send")
    public ResultDTO sendNotify(@RequestBody NotifyCreateReq req){
        try {
            return ResultDTO.of(notificationService.sendNotify(req), ApiResponseCode.SUCCESS.getCode(), "알림 전송", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/read")
    public ResultDTO readNotify(@RequestParam Long receiver ,@RequestParam Long notifId){
        try {
            NotifyRes notifyRes = notificationService.readNotify(receiver,notifId);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "알림 조회", notifyRes);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

}
