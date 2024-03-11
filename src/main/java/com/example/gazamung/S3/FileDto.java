package com.example.gazamung.S3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
public class FileDto {

    private String fileName;
    private String s3Url;
    private MultipartFile file;
}
