package com.example.gazamung.regionFetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApiResponse {

    private String id;
    private List<ResultEntry> result;
    private String errMsg;
    private int errCd;
    private String trId;
}
