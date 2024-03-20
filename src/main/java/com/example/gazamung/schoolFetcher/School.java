package com.example.gazamung.schoolFetcher;

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
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCHOOL_SEQ")
    @SequenceGenerator(name = "SCHOOL_SEQ", sequenceName = "school_sequence", allocationSize = 1)
    private Long id;

    private String schoolName;
    private String regionCode;
//    private String totalCount;
    private String region;

}
