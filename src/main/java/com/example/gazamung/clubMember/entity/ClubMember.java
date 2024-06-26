package com.example.gazamung.clubMember.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLUB_MEMBER_SEQ")
    @SequenceGenerator(name = "CLUB_MEMBER_SEQ", sequenceName = "club_member_sequence", allocationSize = 1)
    private Long clubMemberId;

    private Long memberIdx;
    private Long clubId;

    private LocalDateTime joinedDt;

}
