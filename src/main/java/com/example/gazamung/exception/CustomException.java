package com.example.gazamung.exception;


import com.example.gazamung._enum.CustomExceptionCode;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException {
    private CustomExceptionCode customErrorCode;
    private String detailMessage;

    public CustomException(CustomExceptionCode customExceptionCode) {
        super(customExceptionCode.getStatusMessage());
        this.customErrorCode = customExceptionCode;
        this.detailMessage = customExceptionCode.getStatusMessage();
    }

}
