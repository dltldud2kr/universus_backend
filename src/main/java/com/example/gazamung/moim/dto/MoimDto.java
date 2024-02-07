package com.example.gazamung.moim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MoimDto {

    private Long moimId;

    private Long memberIdx;
    private String title;
    private String content;
    private String location;

    private LocalDateTime regDt;    // 생성일

}
