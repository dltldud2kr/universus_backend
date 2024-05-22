package com.example.gazamung.rank.entity;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RANK_SEQ")
    @SequenceGenerator(name = "RANK_SEQ", sequenceName = "rank_sequence", allocationSize = 1)
    private Long id;

    private Long univId;
    private Long eventId;

    @ColumnDefault("0")
    private Long rankPoint;

    private Long winCount;
    private Long loseCount;


}
