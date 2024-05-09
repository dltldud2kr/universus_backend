package com.example.gazamung.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpelClub {

    private Long memberIdx;
    private Long clubId;

    private Long expelMemberIdx;

}
