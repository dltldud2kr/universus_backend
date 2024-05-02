package com.example.gazamung.club.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubJoinRequest {

    private Long memberIdx;
    private Long clubId;

}
