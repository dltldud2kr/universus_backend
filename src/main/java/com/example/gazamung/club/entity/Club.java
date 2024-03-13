package com.example.gazamung.club.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLUB_SEQ")
    @SequenceGenerator(name = "CLUB_SEQ", sequenceName = "club_sequence", allocationSize = 1)
    private Long clubId;

    private Long memberIdx;
    private String clubName;
    private String content;
    private String location;
    private Long categoryId;
    private String image;
    private Long representIdx;

    private LocalDateTime regDt;    // 생성일
    private LocalDateTime uptDt;

    private Long maximumParticipants;   // 최대 인원

    @ColumnDefault("1")
    private Long currentParticipants;   // 현재 인원

    @ColumnDefault("0")
    private Long bookmarkCnt;

    private Long ageStartLimit;
    private Long ageEndLimit;

}
