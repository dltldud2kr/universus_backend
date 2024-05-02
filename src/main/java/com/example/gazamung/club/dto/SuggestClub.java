package com.example.gazamung.club.dto;

import com.example.gazamung.S3FileUploader.UploadImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestClub {

    private Long clubId;
    private String eventName;
    private String clubName;
    private Long currentMembers;   // 현재 인원
    private String imageUrl; //파일 이미지 URL

}
