package com.example.gazamung.univBattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultResponse {

    private Long univBattleId;
    private Long memberIdx;
    private boolean resultYN;
}
