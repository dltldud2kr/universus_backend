package com.example.gazamung.univBoard.dto;

import com.example.gazamung.S3FileUploader.UploadImage;
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
public class UnivBoardDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateUnivBoardDto {

        private Long univBoardId;      // 게시판 ID

        private Long memberIdx;

        private Long categoryId;        // 카테고리 ID
        private Long univId;            // 대학교
        private Long deptId;            // 학과
        private Long clubId;            // 모임 ID

        private String title;
        private String content;
        private LocalDateTime regDt;    // 생성일
        private LocalDateTime udtDt;

        private List<MultipartFile> postImage;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class InfoUnivBoardDto{

        private Long univBoardId;      // 게시판 ID

        private Long memberIdx;

        private Long categoryId;        // 카테고리 ID
        private Long univId;            // 대학교
        private Long deptId;            // 학과
        private Long clubId;            // 모임 ID

        private String title;
        private String content;
        private LocalDateTime regDt;    // 생성일
        private LocalDateTime udtDt;

        private List<UploadImage> postImage;
    }
}
