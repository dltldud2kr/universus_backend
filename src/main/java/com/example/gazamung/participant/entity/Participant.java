package com.example.gazamung.participant.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARTICIPANT_SEQ")
    @SequenceGenerator(name = "PARTICIPANT_SEQ", sequenceName = "participant_sequence", allocationSize = 1)
    private Long participantId;      // 참가자 ID

    private Long memberIdx;         // 회원
    private Long univId;
    private Long deptId;

    private String userName;
    private Long univBattleId;      // 대결 (학교)
    private Long deptBattleId;      // 대결 (학과)
}
