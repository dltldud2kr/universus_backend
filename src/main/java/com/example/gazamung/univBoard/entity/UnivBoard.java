package com.example.gazamung.univBoard.entity;

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
public class UnivBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UNIVBOARD_SEQ")
    @SequenceGenerator(name = "UNIVBOARD_SEQ", sequenceName = "univboard_sequence", allocationSize = 1)
    private Long univBoardId;      // 게시판 ID

    private Long memberIdx;

    private Long categoryId;        // 카테고리 ID
    private Long univId;            // 대학교
    private Long deptId;            // 학과
    private Long clubId;            // 모임 ID

    private String title;
    private String content;
    private LocalDateTime regDt;    // 생성일
    private LocalDateTime udtDt;    // 수정일

    private String lat;             // 위도
    private String lng;             // 경도
    private String place;           // 위치

    private Long eventId;

    private String matchDt;  // 경기 일시

    private Long representIdx;  // 대표 사진 idx

    private Integer anonymous; // 0 닉네임, 1 익명
}
