package com.example.gazamung.reply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyDto {
    private Long replyId;       // 댓글 ID
    private Long memberIdx;     // 회원 ID

    private String content;     // 내용
    private LocalDateTime lastDt;    // 마지막 작성 시간
}
