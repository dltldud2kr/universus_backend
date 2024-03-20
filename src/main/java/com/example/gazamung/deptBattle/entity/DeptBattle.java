package com.example.gazamung.deptBattle.entity;

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
public class DeptBattle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPT_BATTLE_SEQ")
    @SequenceGenerator(name = "DEPT_BATTLE_SEQ", sequenceName = "dept_battle_sequence", allocationSize = 1)
    private Long deptBattleId;      // 학과 대항전 ID

    private Long univId;            // 주최자 대학
    private Long hostLeader;        // 주최팀 대표
    private Long guestLeader;       // 참가팀 대표
    private Long hostDept;          // 주최팀 학과
    private Long guestDept;         // 참가팀 학과
    private Long winDept;           // 승리팀 학과
    private Long eventId;           // 대결 종목

    private LocalDateTime battleDate;     // 일정
    private String location;        // 장소
    private Long status;            // 상태 _ 0: 종료 , 1: 진행중
    private Long hostScore;         // 주최팀 점수
    private Long guestScore;        // 참가팀 점수
    private LocalDateTime regDt;    // 생성일
    private LocalDateTime endDt;    // 종료일
    private String invitationCode;  // 초대 코드
}
