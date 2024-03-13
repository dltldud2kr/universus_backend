package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ClubRequest {


    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateClubRequestDto {

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

        private List<MultipartFile> clubImage;

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifyClubRequestDto {

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

        private List<MultipartFile> clubImage;

    }



}
