package com.example.gazamung.exception;

public enum SuccessCode {
    SELECT_SUCCESS(200, "데이터 조회 성공"),
    UPDATE_SUCCESS(200, "데이터 업데이트 성공"),
    CREATE_SUCCESS(201, "데이터 생성 성공");

    private final int status;
    private final String message;

    SuccessCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}