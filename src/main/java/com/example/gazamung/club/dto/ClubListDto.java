package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.security.auth.callback.LanguageCallback;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubListDto {
    
    
    private Long clubId; // 삭제 ㄴㄴㄴㄴㄴㄴㄴㄴ

    private String eventName;           // 종목 ID

    private String clubName;        // 모임명
    private String introduction;    // 모임 소개

    private Long currentMembers;

    private String clubImageUrl; //파일 이미지 URL

    private Long joinedStatus; // 0 미가입, 1 가입

    private LocalDateTime joinedDt; // 가입 날짜

}
