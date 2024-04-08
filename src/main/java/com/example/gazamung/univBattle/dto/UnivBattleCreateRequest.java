package com.example.gazamung.univBattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnivBattleCreateRequest {

    private long hostLeader ;       // 대표자 memberIdx
    private long eventId;           // 종목ID
    private String lat;             // 위도
    private String lng;             // 경도
    private String place;           // 위치
    private String battleDate;      // 일정
    private String content;         // 내용
    private String cost;            // 비용
    private int teamPtcLimit;  // 참가 제한인원
}
