package com.example.gazamung.meeting.dto;

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
public class MeetingRequest {


    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateMeetingRequestDto {

        private Long memberIdx;
        private Long clubId;
        private String content;
        private String location;
        private int cost;
        private String schedule;
        private String representImgIdx;

        private LocalDateTime regDt;    // 생성일

        private int endDt;  // 모집기간
        private int maximumParticipants;   // 총 인원

        private List<MultipartFile> meetingImages;

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModifyMeetingRequestDto {

        private Long memberIdx;
        private Long clubIdx;
        private String clubName;
        private String content;
        private String location;
        private Long categoryId;

        private LocalDateTime regDt;    // 생성일
        private LocalDateTime uptDt;

        private Long maximumParticipants;   // 총 인원
        private Long currentParticipants;   // 현재 인원

        private Long bookmarkCnt;

        private Long ageStartLimit;
        private Long ageEndLimit;

        private List<MultipartFile> clubImage;

    }



}
