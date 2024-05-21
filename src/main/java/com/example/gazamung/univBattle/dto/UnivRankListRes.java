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
public class UnivRankListRes {


    private Long univId;
    private Long eventId;           // 대결 종목
    private String univName;
    private String univLogo;   // 참가팀 로고
    private long winCount;
    private long loseCount;
    private long matchCount;

}
