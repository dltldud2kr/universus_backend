package com.example.gazamung.notification.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTIFICATION_SEQ")
    @SequenceGenerator(name = "NOTIFICATION_SEQ", sequenceName = "notification_sequence", allocationSize = 1)
    private Long notifId;       // 알림 ID

    private Long receiver;     // 수신자
    private Long caller;    // 발신자

    private String content;     // 내용
    private Long type;          // 유형 ... 0 : 댓글, 1 : 경기
    private LocalDateTime timeStamp;    // 발생 시간
    private Long isRead;        // 읽음 여부

    private Long relatedItemId; // 관련 항목 ... ?????
}
