package com.example.gazamung.regionFetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResultEntry {

    private String y_coor;
    private String full_addr;
    private String x_coor;
    private String addr_name;
    private String cd;
}
