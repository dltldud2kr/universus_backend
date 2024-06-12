package com.example.gazamung.announcement;

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
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANNOUNCEMENT_SEQ")
    @SequenceGenerator(name = "ANNOUNCEMENT_SEQ", sequenceName = "announcement_sequence", allocationSize = 1)
    private Long idx;

    private Long memberIdx;
    private String title;
    private String content;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;
}
