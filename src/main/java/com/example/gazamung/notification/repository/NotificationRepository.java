package com.example.gazamung.notification.repository;


import com.example.gazamung.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByReceiverAndNotifId(Long receiver, Long notifId);
    List<Notification> findByReceiver(Long memberIdx);
}
