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
public class CreateDto {

    private Long replyId;       // 댓글 ID

    private Long memberIdx;     // 회원 ID
    private Long univBoardId;   // 게시글 ID

    private String content;     // 내용

    private LocalDateTime lastDt;
}
