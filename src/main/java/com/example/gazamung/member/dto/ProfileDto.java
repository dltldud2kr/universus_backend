package com.example.gazamung.member.dto;

import com.example.gazamung.S3FileUploader.UploadImage;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProfileDto {

    private Long memberIdx;

    private String userName;
    private String nickname;
    private String phone;

    private String oneLineIntro;    // 한 줄 소개

    private String schoolName;  // 대학명
    private String deptName;        // 학과명

    private String logoImg;

    private List<UploadImage> profileImage;


}
