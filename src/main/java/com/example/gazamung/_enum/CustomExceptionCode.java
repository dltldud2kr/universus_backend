package com.example.gazamung._enum;

public enum CustomExceptionCode {

    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "권한이 없는 사용자입니다."),
    EXPIRED_JWT("EXPIRED_JWT","만료된 토큰입니다."),
    PAYMENT_EXPIRED_JWT("PAYMENT_EXPIRED_JWT", "만료된 결제 토큰입니다."),
    INVALID_JWT("INVALID_JWT", "올바른 형식의 토큰이 아닙니다."),
    NOT_FOUND_EMAIL("NOT_FOUND_EMAIL", "이메일을 찾을 수 없습니다."),
    NOT_FOUND_REPLIES("NOT_FOUND_REPLIES", "댓글을 찾을 수 없습니다."),
    NOT_FOUND_USER("NOT_FOUND_USER", "가입되지 않은 회원입니다."),
    NOT_FOUND("NOT_FOUND","해당 정보를 찾을 수 없습니다."),
    NOT_FOUND_BOARD("NOT_FOUND","해당 게시글을 찾을 수 없습니다."),
    DUPLICATED("DUPLICATED","중복된 정보가 존재합니다."),
    NOT_ENOUGH_ITEMS("NOT_ENOUGH_ITEMS","선택지 항목이 2개 이상이어야 합니다.");


    // 추후에 추가될 다른 업로드 타입들

    private final String statusCode;
    private final String statusMessage;

    CustomExceptionCode(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
    public String getStatusCode() {
        return statusCode;
    }
    public String getStatusMessage() {
        return statusMessage;
    }

}
