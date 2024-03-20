package com.example.gazamung.reply.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARTICIPANT_SEQ")
    @SequenceGenerator(name = "PARTICIPANT_SEQ", sequenceName = "participant_sequence", allocationSize = 1)
    private Long replyId;       // 댓글 ID

    private Long memberIdx;     // 회원 ID
    private Long univBoardId;   // 게시판 ID

    private String content;     // 내용
}
