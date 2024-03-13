package com.example.gazamung.meeting;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEETING_SEQ")
    @SequenceGenerator(name = "MEETING_SEQ", sequenceName = "meeting_sequence", allocationSize = 1)
    private Long meetingId;

    private Long clubId;        // 모임 idx
    private Long memberIdx;     // 회원idx
    private String title;       // 제목
    private String schedule;    // 일정  3.15(금) 오후 7:30
    private String content;     // 내용
    private Long representImgIdx;  // 대표 사진 idx
    private String location;    // 장소
    private int cost;   //비용
    private int currentParticipants;    //현재인원
    private int maxParticipants;    //최대인원
    private int endDt;    //모집기간  ex) 1, 2, 3 ... 일
    private LocalDateTime regDt;    // 생성일





}
