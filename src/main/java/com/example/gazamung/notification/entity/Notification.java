package com.example.gazamung.notification.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Notification {
    @Id
    @GeneratedValue
    private Long notificationId;

    private LocalDateTime createdAt;
    private Long memberIdx;
    private String content; // 메시지 내용
    private Long moimId;



}