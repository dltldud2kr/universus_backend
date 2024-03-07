package com.example.gazamung.group.entity;

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
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_SEQ")
    @SequenceGenerator(name = "GROUP_SEQ", sequenceName = "group_sequence", allocationSize = 1)
    private Long groupId;

    private Long memberIdx;
    private String title;
    private String content;
    private String location;
    private Long categoryId;
    private String image;

    private LocalDateTime regDt;    // 생성일

    private Long maximumParticipants;   // 최대 인원
    private Long currentParticipants;   // 현재 인원

    @ColumnDefault("0")
    private Long bookmarkCnt;

}
