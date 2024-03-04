package com.example.gazamung.moim.entity;

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
public class Moim {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MOIM_SEQ")
    @SequenceGenerator(name = "MOIM_SEQ", sequenceName = "moim_sequence", allocationSize = 1)
    private Long moimId;

    private Long memberIdx;
    private String title;
    private String content;
    private String location;
    private Long categoryId;

    private LocalDateTime regDt;    // 생성일

    private Long maximumParticipants;   // 총 인원
    private Long currentParticipants;   // 현재 인원

    @ColumnDefault("0")
    private Long likeCnt;

    public void incrementLikeCount() {
        this.likeCnt++;
    }

    public void decrementLikeCount() {
        this.likeCnt--;
    }
}
