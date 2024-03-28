package com.example.gazamung.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UpdateProfileDto {

    private Long memberIdx;

    private List<MultipartFile> profileImage;

}
