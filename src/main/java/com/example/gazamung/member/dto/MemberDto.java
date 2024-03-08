package com.example.gazamung.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class MemberDto {

    private Long memberIdx;

    private String email;
    private String password;
    private String refreshToken; //리프레쉬 토큰
    private String userName;
    private String nickname;
    private String phone;
    private String address;
    private Integer role;   // 0 : USER 1 : ADMIN
    private int platform; // 0.가자멍 1. 카카오
    private LocalDateTime regDt;
    private LocalDateTime udtDt;

}
