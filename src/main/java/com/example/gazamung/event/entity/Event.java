package com.example.gazamung.event.entity;

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
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVENT_SEQ")
    @SequenceGenerator(name = "EVENT_SEQ", sequenceName = "event_sequence", allocationSize = 1)
    private Long eventId;      // 종목 ID

    private String eventName;   // 종목명
}
