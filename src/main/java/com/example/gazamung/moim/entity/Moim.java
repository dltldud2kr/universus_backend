package com.example.gazamung.moim.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    @GeneratedValue
    private Long moimId;

    private Long memberIdx;
    private String title;
    private String content;
    private String location;

    private LocalDateTime regDt;    // 생성일


}
