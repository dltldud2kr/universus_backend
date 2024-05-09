package com.example.gazamung.notification.entity;

import com.example.gazamung._enum.MsgType;
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

    @Enumerated(EnumType.STRING)
    private MsgType type;          // 유형   0:대학  1:과  2: 댓글


    private LocalDateTime regDt;    // 발생 시간

    @Column(name = "IS_READ", nullable = false)
    private boolean isRead;  // 자동으로 0과 1을 매핑

    @Column(name = "TARGET_URL")
    private String targetUrl;  // 사용자를 이동시킬 URL

    private Long relatedItemId; // 관련 항목 ... ?????
}
