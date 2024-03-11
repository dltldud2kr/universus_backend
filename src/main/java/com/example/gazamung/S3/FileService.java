package com.example.gazamung.S3;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileUploadRepository fileRepository;

    public void save(FileDto fileDto) {
        FileUpload fileUpload = FileUpload.builder()
                .fileName(fileDto.getFileName())
                .s3Url(fileDto.getS3Url())
                .build();
        fileRepository.save(fileUpload);
    }

    public List<FileUpload> getFiles() {
        List<FileUpload> all = fileRepository.findAll();
        return all;
    }
}