package com.example.gazamung.search;

import com.example.gazamung.club.entity.Club;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
@AllArgsConstructor
public class ClubSearchResult {

        private Club club;
        private String imageUrl;



}
