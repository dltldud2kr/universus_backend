package com.example.gazamung.deptBattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptMatchResultRes {

    private Long deptBattleId;
    private boolean resultYN;
}
