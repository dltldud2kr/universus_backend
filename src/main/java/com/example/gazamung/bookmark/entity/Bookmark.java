package com.example.gazamung.bookmark.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class Bookmark {

    @Id
    @GeneratedValue
    private long bookMarkId;

    private long memberIdx;
    private long clubId;

}
