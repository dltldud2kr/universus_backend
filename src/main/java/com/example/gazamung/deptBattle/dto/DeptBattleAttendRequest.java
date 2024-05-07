package com.example.gazamung.deptBattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptBattleAttendRequest {

    private long deptBattleId;   // 해당 대항전 ID 값
    private long memberIdx ;   // memberIdx
    private String invitationCode;  // 초대코드


}
