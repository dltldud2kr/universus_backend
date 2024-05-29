package com.example.gazamung.rank.dto;

import com.example.gazamung._enum.MsgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnivRankRes {

    private Long univId;
    private Long eventId;

    private Long rankPoint;

    private String schoolName;
    private Long winCount;
    private Long loseCount;
    private Long totalCount;

}
