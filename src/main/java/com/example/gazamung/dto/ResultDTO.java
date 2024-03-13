package com.example.gazamung.dto;

import com.example.gazamung._enum.ApiResponseCode;
import com.example.gazamung.category.dto.CategoryDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class ResultDTO<D> {
    private final boolean success;
    private final String resultCode;
    private final String message;
    private final D data;

}

