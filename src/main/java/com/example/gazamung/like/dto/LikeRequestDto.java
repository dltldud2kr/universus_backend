package com.example.gazamung.like.dto;

import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeRequestDto {

    private Long memberIdx;
    private Long moimId;

    public LikeRequestDto(Long memberIdx, Long moimId){
        this.memberIdx = memberIdx;
        this.moimId = moimId;
    }
}
