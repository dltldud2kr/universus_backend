package com.example.gazamung.notification.service;

import com.example.gazamung.notification.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    void sendNotification(Long moimId, Long memberIdx);

    List<Notification> getJoinRequest(Long moimId);
}
