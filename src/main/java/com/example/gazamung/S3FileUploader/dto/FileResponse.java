package com.example.gazamung.S3FileUploader.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
@AllArgsConstructor
public class FileResponse {
    @Builder
    @Data
    @AllArgsConstructor
    public static class ReviewImageDto {
        private String fileName;
        private Long fileSize;
        private String imageUrl;

    }

}
