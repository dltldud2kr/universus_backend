package com.example.gazamung.member.dto;

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

    private Long univId;    //대학
    private Long deptId;    //학과

    private List<MultipartFile> profileImage;

}
