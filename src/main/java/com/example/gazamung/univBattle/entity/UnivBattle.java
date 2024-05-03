package com.example.gazamung.univBattle.entity;

import com.example.gazamung._enum.MatchStatus;
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


    private String hostUnivName;
    private String guestUnivName;
    private String hostUnivLogo;    // 주최팀 로고
    private String guestUnivLogo;   // 참가팀 로고
    private String battleDate;     // 일정
    private LocalDateTime matchStartDt; // 경기 시작시간
    private LocalDateTime matchEndDt;   // 경기 종료시간

    private String lat;             // 위도
    private String lng;             // 경도
    private String place;           // 위치


    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;           // 상태 -> 진행중 , 대기중, 모집중 , 종료

    private Long hostScore;         // 주최팀 점수
    private Long guestScore;        // 참가팀 점수
    private int teamPtcLimit;   // 팀당 제한 인원
    private LocalDateTime regDt;    // 생성일
    private LocalDateTime endDt;    // 종료일
    private String invitationCode;  // 초대 코드

}
