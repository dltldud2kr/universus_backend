package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubMembersDto {

    private Long memberIdx;
    private String nickname;
    private String profileImgUrl;
    private LocalDateTime joinedDt;

}
