package com.example.gazamung.univcert.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CertificationCodeRequest {

    private String key;
    private String email;
    private String univName;
    private int code;
}
