package com.example.gazamung.support;

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
public class Support {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUPPORT_SEQ")
    @SequenceGenerator(name = "SUPPORT_SEQ", sequenceName = "support_sequence", allocationSize = 1)
    private Long idx;

    private Long memberIdx;
    private String title;
    private String content;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;
}
