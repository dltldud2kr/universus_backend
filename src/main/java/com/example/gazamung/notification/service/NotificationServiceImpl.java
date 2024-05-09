package com.example.gazamung.notification.service;

import com.example.gazamung._enum.MsgType;
import com.example.gazamung.notification.dto.NotifyCreateReq;
import com.example.gazamung.notification.entity.Notification;
import com.example.gazamung.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean sendNotify(NotifyCreateReq request) {


        Notification notification = Notification.builder()
                .caller(request.getCaller())
                .receiver(request.getReceiver())
                .type(request.getType())
                .content(request.getContent())
                .isRead(request.isRead())
                .relatedItemId(request.getRelatedItemId())
                .regDt(LocalDateTime.now())
                .targetUrl(generateTargetUrl(request.getType(),request.getRelatedItemId()))
                .build();

        notificationRepository.save(notification);

        return true;
    }



    public String generateTargetUrl(MsgType type, Long relatedId) {
        switch (type) {
            case UNIV_BATTLE:
                return "/api/v1/univBattle/info/" + relatedId;
            case DEPT_BATTLE:
                return "/api/v1/deptBattle/info/" + relatedId;
            case POST_COMMENT:
                return "/api/v1/univBoard/info/" + relatedId;
            default:
                return "";
        }
    }

}