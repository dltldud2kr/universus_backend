package com.example.gazamung.search;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class searchRes {
    private Long univBoardId;
    private String title;
    private String content;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime regDt;
}
