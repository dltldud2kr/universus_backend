package com.example.gazamung.exception;


import com.example.gazamung._enum.CustomExceptionCode;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException {
    private CustomExceptionCode customErrorCode;
    private String detailMessage;
    private Map<String, Object> data;


    public CustomException(CustomExceptionCode customExceptionCode) {
        super(customExceptionCode.getStatusMessage());
        this.customErrorCode = customExceptionCode;
        this.detailMessage = customExceptionCode.getStatusMessage();
    }

    public CustomException(CustomExceptionCode customExceptionCode, Map<String, Object> data) {
        super(customExceptionCode.getStatusMessage());
        this.customErrorCode = customExceptionCode;
        this.detailMessage = customExceptionCode.getStatusMessage();
        this.data = data; // 데이터 필드 초기화
    }

    public CustomException(CustomExceptionCode customExceptionCode, String detailMessage) {
        super(detailMessage);
        this.customErrorCode = customExceptionCode;
        this.detailMessage = detailMessage;
    }

}
