package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
