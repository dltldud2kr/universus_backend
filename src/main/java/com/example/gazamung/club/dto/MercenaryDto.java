package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MercenaryDto {

    private Long univBoardId;

    private String title;
    private String eventName;

    private String lat;             // 위도
    private String lng;             // 경도
    private String place;           // 위치

    private String imageUrl; //파일 이미지 URL

    private String matchDt;  // 경기 일시

}
