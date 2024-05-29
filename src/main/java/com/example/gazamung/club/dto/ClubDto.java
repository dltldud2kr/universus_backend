package com.example.gazamung.club.dto;

import com.example.gazamung.S3FileUploader.UploadImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubDto {

    private Long clubId; // 클럽 id
    private String nickname;         // 대표자 명
    private String memberImageUrl;
    private String oneLineIntro;    // 한 줄 소개

    private String  eventName;           // 종목 ID

    private String clubName;        // 모임명
    private String introduction;    // 모임 소개
    private LocalDateTime regDt;    // 생성일
    private Long price;             // 비용

    private Long maximumMembers;   // 총 인원
    private Long currentMembers;   // 현재 인원

    private List<String> clubImageUrls;

    private Long joinedStatus; // 0 미가입, 1 가입


}

