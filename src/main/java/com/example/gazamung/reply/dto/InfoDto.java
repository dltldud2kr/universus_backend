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
public class InfoDto {


    private Long replyId;       // 댓글 ID
    private Long memberIdx;

    private String content;     // 내용

    private LocalDateTime lastDt;

    private String profileImgUrl;

    private String nickOrAnon;

}
