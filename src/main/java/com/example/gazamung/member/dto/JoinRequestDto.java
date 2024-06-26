package com.example.gazamung.member.dto;


import lombok.Data;

@Data
public class JoinRequestDto {
    private String email; //이메일
    private String password; //비밀번호
    private String userName;    // 사용자 이름
    private String birth;
    private String gender;
    private Long deptId;
    private String nickname;    // 닉네임
    private String phone;   // 전화번호
    private String address; // 주소
    private Long univId;    // 대학 id
}
