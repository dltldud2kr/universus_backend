package com.example.gazamung.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class updateNickNameDto {

    private Long memberIdx;
    private String nickname;

}
