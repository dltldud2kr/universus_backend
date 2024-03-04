package com.example.gazamung.notification.repository;

import com.example.gazamung.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByMoimId(Long moimId);

    Long findByMemberIdx(Long notificationId);
}
