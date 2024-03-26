package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubDto {

    private Long clubId;            // 모임 ID

    private Long memberIdx;         // 대표자
    private Long eventId;           // 종목 ID
    private Long univId;            // 대학 ID

    private String clubName;        // 모임명
    private String introduction;    // 모임 소개
    private LocalDateTime regDt;    // 생성일
    private Long price;             // 비용

    private Long maximumMembers;   // 총 인원
    private Long currentMembers;   // 현재 인원

    private MultipartFile clubImage;
}

