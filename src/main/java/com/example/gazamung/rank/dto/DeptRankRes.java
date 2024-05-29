package com.example.gazamung.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptRankRes {

    private Long deptId;
    private Long eventId;

    private Long rankPoint;

    private Long winCount;
    private Long loseCount;
    private Long totalCount;

}