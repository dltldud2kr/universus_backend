package com.example.gazamung.emailAuth.dto;


import com.example.gazamung.exception.CustomErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;



@Builder
@Data
@AllArgsConstructor
public class EmailDto {

    private String email;
    private CustomErrorResponse errorResponse;
}
