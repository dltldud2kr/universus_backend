package com.example.gazamung.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationDto {
    private Long notificationId;

    private LocalDateTime createdAt;
    private Long memberIdx;
    private String content; // 메시지 내용
    private Long moimId;


}
