package com.example.gazamung.reply.entity;

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
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLY_SEQ")
    @SequenceGenerator(name = "REPLY_SEQ", sequenceName = "reply_sequence", allocationSize = 1)
    private Long replyId;       // 댓글 ID

    private Long memberIdx;     // 회원 ID
    private Long univBoardId;   // 게시글 ID

    private String content;     // 내용

    private LocalDateTime lastDt;    // 마지막 작성 시간
}
