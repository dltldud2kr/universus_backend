package com.example.gazamung.exception;

import com.example.gazamung._enum.CustomExceptionCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorResponse {
    private CustomExceptionCode status;
    private String statusMessage;
}
