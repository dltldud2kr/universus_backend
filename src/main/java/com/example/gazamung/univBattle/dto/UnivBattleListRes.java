package com.example.gazamung.univBattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnivBattleListRes {


    private long univBattleId;   // 해당 대항전 ID 값
//    private Long winUniv;           // 승리팀 대학교
//    private Long loseUniv;
    private Long eventId;           // 대결 종목
    private String hostUnivName;
    private String guestUnivName;
    private String hostUnivLogo;    // 주최팀 로고
    private String guestUnivLogo;   // 참가팀 로고
    private String battleDate;     // 일정
    private LocalDateTime matchEndDt;   // 경기 종료시간
    private LocalDateTime matchStartDt; // 경기 시작시간
    private Long hostScore;         // 주최팀 점수
    private Long guestScore;        // 참가팀 점수

    private String result;          // 해당 대학 경기 결과에 따른 승리,패배 체크  (lose,win)

}
