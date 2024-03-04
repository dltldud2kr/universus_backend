package com.example.gazamung.moim.dto;

import com.example.gazamung.moim.entity.Moim;
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
    private Long categoryId;

    private LocalDateTime regDt;    // 생성일

    private Long maximumParticipants;   // 총 인원
    private Long currentParticipants;   // 현재 인원

    private Long likeCnt;

    // MoimDto 클래스에 convertToDto 메서드 추가
    public static MoimDto convertToDto(Moim moim) {
        return MoimDto.builder()
                .moimId(moim.getMoimId())
                .memberIdx(moim.getMemberIdx())
                .title(moim.getTitle())
                .content(moim.getContent())
                .location(moim.getLocation())
                .categoryId(moim.getCategoryId())
                .regDt(moim.getRegDt())
                .build();
    }

}
