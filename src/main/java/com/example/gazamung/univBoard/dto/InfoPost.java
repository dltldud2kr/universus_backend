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

    private Long memberIdx;
    private String categoryName;        // 카테고리 ID
    private String clubName;        // 모임명

    private String title;
    private String content;
    private LocalDateTime regDt;    // 생성일
    private LocalDateTime udtDt;

    private List<String> postImageUrls;

}
