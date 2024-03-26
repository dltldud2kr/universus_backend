package com.example.gazamung.univBattle.entity;

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
public class UnivBattle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UNIV_BATTLE_SEQ")
    @SequenceGenerator(name = "UNIV_BATTLE_SEQ", sequenceName = "univ_battle_sequence", allocationSize = 1)
    private Long univBattleId;      // 대학 대항전 ID

    private Long hostLeader;         // 주최팀 대표
    private Long guestLeader;        // 참가팀 대표
    private Long hostUniv;          // 주최팀 대학교
    private Long guestUniv;         // 참가팀 대학교
    private Long winUniv;           // 승리팀 대학교
    private Long eventId;           // 대결 종목
    private String content;         // 내용
    private String cost;

    private String battleDate;     // 일정
    private String location;        // 장소
    private int status;            // 상태  0: 종료 , 1: 진행중
    private Long hostScore;         // 주최팀 점수
    private Long guestScore;        // 참가팀 점수
    private LocalDateTime regDt;    // 생성일
    private LocalDateTime endDt;    // 종료일
    private String invitationCode;  // 초대 코드

}
