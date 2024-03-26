package com.example.gazamung.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListDto {

    private Long eventId;      // 종목 ID
    private String eventName;   // 종목명

}
