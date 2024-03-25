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
    private Long clubId;            // 모임 ID

    private Long memberIdx;         // 대표자
    private Long eventId;           // 종목 ID
    private Long univId;            // 대학 ID

    private String clubName;        // 모임명
    private String introduction;    // 모임 소개
    private LocalDateTime regDt;    // 생성일
    private Long price;             // 비용

}
