package com.example.gazamung.regionFetcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccessTokenResponse {

    @JsonProperty("result")
    private Result result;

    @Setter
    @Getter
    public static class Result {
        @JsonProperty("accessToken")
        private String accessToken;
    }

}