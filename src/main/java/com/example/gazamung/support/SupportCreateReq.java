package com.example.gazamung.support;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportCreateReq {

    private Long memberIdx;
    private String title;
    private String content;

}
