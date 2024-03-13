package com.example.gazamung.S3FileUploader;


import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung._enum.AttachmentType;
import com.example.gazamung.dto.ResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
@Tag(name = "업로드 관련", description = "업로드 관련(리뷰이미지,프로필이미지 등 관련 API)")
@CrossOrigin(origins = "*", exposedHeaders = {"Content-Disposition"}, allowedHeaders = "*")
public class UploadController {
    private final UploadService uploadService;


    @PutMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultDTO<Map<String,Object>> saveImage(
            @RequestParam(value="image") List<MultipartFile> image,
            @RequestParam long memberIdx,
    @RequestParam long mappedId) throws IOException {

        Map<String,Object> sampleArray = new HashMap<>();
        uploadService.upload(image,memberIdx , AttachmentType.REVIEW,mappedId);
        return ResultDTO.of(true, ApiResponseCode.CREATED.getCode(),ApiResponseCode.CREATED.getMessage(), sampleArray);
    }

    @GetMapping("/image/{id}")
    public ResultDTO<UploadImage> getImageById(@PathVariable Long id) {
        try{
            Optional<UploadImage> image = uploadService.getImageById(id);
            if (!image.isPresent()) {
                return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), null);
            } else {
                return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), image.get());
            }
        } catch(NullPointerException error) {
            Optional<UploadImage> image = uploadService.getImageById(id);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), image.get());
        }
    }

    @GetMapping("/image/{attachment}/{mappedId}")
    public ResultDTO<List<UploadImage>> getImageByAttachmentType(
            @PathVariable String attachment,
            @PathVariable long mappedId) {
        List<UploadImage> results = uploadService.getImageByAttachmentType(AttachmentType.fromString(attachment),mappedId);
        return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), ApiResponseCode.SUCCESS.getMessage(), results);
    }
}
