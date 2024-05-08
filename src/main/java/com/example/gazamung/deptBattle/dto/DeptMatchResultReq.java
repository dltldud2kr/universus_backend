package com.example.gazamung.deptBattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptMatchResultReq {

    private Long deptBattleId;
    private Long hostLeader;         // 주최팀 대표

    private Long winDept;           // 승리팀 대학교
    private Long hostScore;         // 주최팀 점수
    private Long guestScore;        // 참가팀 점수
}
