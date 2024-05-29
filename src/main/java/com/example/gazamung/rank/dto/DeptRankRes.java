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
    private Long univId;
    private Long eventId;
    private String deptName;

    private Long rankPoint;

    private Long winCount;
    private Long loseCount;
    private Long totalCount;

}
