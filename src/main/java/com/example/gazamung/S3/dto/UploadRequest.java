package com.example.gazamung.S3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UploadRequest {
    private MultipartFile uploadFile;
    private String uuid;
    private String code;

}
