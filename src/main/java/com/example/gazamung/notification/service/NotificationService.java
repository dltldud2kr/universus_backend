package com.example.gazamung.notification.service;

import com.example.gazamung.fcmSend.FcmSendDto;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface NotificationService {

    boolean sendNotify(NotifyCreateReq request);

}