package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubListDto {

    private String eventName;           // 종목 ID

    private String clubName;        // 모임명
    private String introduction;    // 모임 소개

    private Long currentMembers;

    private String clubImageUrl; //파일 이미지 URL

}
