package com.example.gazamung.announcement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementUpdateReq {

    private Long memberIdx;
    private String title;
    private String content;
}
