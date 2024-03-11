package com.example.gazamung.club.dto;

import com.example.gazamung.club.entity.Club;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ClubDto {

    private Long clubId;

    private Long memberIdx;
    private String clubName;
    private String content;
    private String location;
    private Long categoryId;

    private LocalDateTime regDt;    // 생성일

    private Long maximumParticipants;   // 총 인원
    private Long currentParticipants;   // 현재 인원

    private Long bookmarkCnt;

    private Long ageStartLimit;
    private Long ageEndLimit;


}
