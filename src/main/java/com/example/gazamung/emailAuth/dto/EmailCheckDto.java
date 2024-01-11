package com.example.gazamung.emailAuth.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmailCheckDto {

    private String email;
    private String verifcode;
}
