package com.example.gazamung.member.dto;


import lombok.Data;

@Data
public class JoinRequestDto {
    private String email; //이메일
    private String password; //비밀번호
    private String phone;   // 전화번호
    private String addressLine1;
    private String addressLine2;
    private int platform; //가입 플랫폼 0:flatform 1:kakao 2:naver...
}
