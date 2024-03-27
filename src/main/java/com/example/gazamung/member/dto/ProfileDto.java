package com.example.gazamung.member.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProfileDto {


    private String nickName;
    private String email;
    private int platform;
    private String areaIntrs;

}
