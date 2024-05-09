package com.example.gazamung.notification.controller;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.dto.ResultDTO;
import com.example.gazamung.exception.CustomException;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.service.NotificationService;
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

    @PostMapping("/send")
    public ResultDTO sendNotify(@RequestBody NotifyCreateReq req){
        try {
            return ResultDTO.of(notificationService.sendNotify(req), ApiResponseCode.SUCCESS.getCode(), "대항전 정보", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

}
