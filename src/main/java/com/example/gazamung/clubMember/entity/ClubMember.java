package com.example.gazamung.clubMember.entity;

import com.example.gazamung._enum.ClubRank;
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
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLUB_MEMBER_SEQ")
    @SequenceGenerator(name = "CLUB_MEMBER_SEQ", sequenceName = "club_member_sequence", allocationSize = 1)
    private Long clubMemberId;

    private Long memberIdx;
    private Long clubId;

}
