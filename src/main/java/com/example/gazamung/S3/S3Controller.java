package com.example.gazamung.S3;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "S3파일업로드 API", description = "")
public class S3Controller {

    private final S3Uploader s3Uploader;
    private final FileService fileService;



    @PostMapping("/upload")
    public String uploadFile(FileDto fileDto) throws IOException {
        String url = s3Uploader.uploadFile(fileDto.getFile());

        fileDto.setS3Url(url);
        fileService.save(fileDto);

        return "good";
    }

//    @GetMapping("/api/list")
//    public String listPage(Model model) {
//        List<FileEntity> fileList =fileService.getFiles();
//        model.addAttribute("fileList", fileList);
//        return "list";
//    }
}
