package com.example.gazamung.notification.dto;


import com.example.gazamung._enum.MsgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyCreateReq {

    private Long receiver;     // 수신자
    private Long caller;    // 발신자
    private String title;
    private String content;     // 내용
    private MsgType type;          // 유형   0:대학  1:과  2: 댓글
    private boolean isRead;        // 읽음 여부
    private Long relatedItemId; // 관련 항목 ... ?????

}
