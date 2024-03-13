package com.example.gazamung.member.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProfileDto {

    //todo 가입된 모임 수, 작성된 후기게시글 수?( 미정 ),  프로필 이미지 URL 필요

    private String nickName;
    private String email;
    private int platform;
    private String areaIntrs;

}
