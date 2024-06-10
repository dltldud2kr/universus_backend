package com.example.gazamung.univBoard.dto;

import com.example.gazamung.S3FileUploader.UploadImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class InfoPost {

    private Long univBoardId;
    private String nickOrAnon;
    private String categoryName;
    private Long categoryId;
    private String clubName;

    private String title;
    private String content;
    private LocalDateTime regDt;    // 생성일
    private LocalDateTime udtDt;

    private List<String> postImageUrls;

    private String profileImgUrl;

    private String lat;             // 위도
    private String lng;             // 경도
    private String place;           // 위치

    private String eventName;

    private String matchDt;  // 경기 일시
    private Long memberIdx; // 글쓴이 memberIdx 삭제 금지!

    private Long eventId;
}
