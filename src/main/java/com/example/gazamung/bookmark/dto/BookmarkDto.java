package com.example.gazamung.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookmarkDto {

    private long bookMarkId;

    private long memberIdx;
    private long clubId;
}
