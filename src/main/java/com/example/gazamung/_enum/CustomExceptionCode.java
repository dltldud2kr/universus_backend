package com.example.gazamung._enum;

import org.springframework.http.HttpStatus;

public enum CustomExceptionCode {

    UNAUTHORIZED_USER("UNAUTHORIZED_USER", "권한이 없는 사용자입니다.", HttpStatus.BAD_REQUEST),
    EXPIRED_JWT("EXPIRED_JWT","만료된 토큰입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_EXPIRED_JWT("PAYMENT_EXPIRED_JWT", "만료된 결제 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_JWT("INVALID_JWT", "올바른 형식의 토큰이 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_EMAIL("NOT_FOUND_EMAIL", "이메일을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_REPLIES("NOT_FOUND_REPLIES", "댓글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_USER("NOT_FOUND_USER", "가입되지 않은 회원입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("NOT_FOUND","해당 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_BOARD("NOT_FOUND","해당 게시글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("SERVER_ERROR", "요청중 서버 문제가 발생했습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED("DUPLICATED","중복된 정보가 존재합니다.", HttpStatus.BAD_REQUEST),
    SAME_NICKNAME("DUPLICATED_NICKNAME","이미 같은 닉네임을 사용중입니다.", HttpStatus.BAD_REQUEST),
    DIFFERENT_PASSWORD("DIFFERENT_PASSWORD","잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    CHARACTER_LIMIT("CHARACTER_LIMIT", "글자 수 제한", HttpStatus.BAD_REQUEST),


    //대항전
    NOT_FOUND_BATTLE("NOT_FOUND_BATTLE","존재하지 않는 대항전", HttpStatus.BAD_REQUEST),
    SAME_UNIVERSITY("SAME_UNIVERSITY","같은 대학교는 참가할 수 없습니다.", HttpStatus.BAD_REQUEST),
    EXCEEDED_TOTAL_CAPACITY("EXCEEDED_CAPACITY","대항전 총 참가 인원이 초과하였습니다.", HttpStatus.BAD_REQUEST),
    EXCEEDED_UNIV_CAPACITY("EXCEEDED_UNIV_CAPACITY","대항전 대학별 참가 인원이 초과하였습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INVITE_CODE("INVALID_INVITE_CODE","참가 코드가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ATTENDED("ALREADY_ATTENDED","이미 참가한 회원입니다.", HttpStatus.BAD_REQUEST),

    //채팅
    INVALID_URI("INVALID_URI","올바른 형식의 URI가 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_HEADER_DATA("NOT_FOUND_HEADER_DATA","헤더에 memberIdx 데이터 값이 없습니다.", HttpStatus.BAD_REQUEST),

    //대학
    NOT_FOUND_UNIVERSITY("NOT_FOUND_UNIVERSITY","존재하지 않는 대학교입니다.", HttpStatus.BAD_REQUEST),


    //클럽
    AGE_LIMIT_EXCEEDED("AGE_LIMIT_EXCEEDED","가입조건에 충족하지 않은 나이", HttpStatus.BAD_REQUEST),
    MEMBERSHIP_LIMIT_EXCEEDED("MEMBERSHIP_LIMIT_EXCEEDED", "모임 가입 개수를 초과", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_MEMBER("ALREADY_REGISTERED_MEMBER", "이미 가입된 회원입니다", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("ACCESS_DENIED","관리자 외 접근 거부", HttpStatus.BAD_REQUEST),
    NOT_MATCHED_UNIVERSITY("NOT_MATCHED_UNIVERSITY","같은 대학이어야합니다.", HttpStatus.BAD_REQUEST),
    YOU_ARE_MASTER("YOU_ARE_MASTER","본인이 생성한 모임입니다.", HttpStatus.BAD_REQUEST),



    //메일 인증
    DUPLICATED_MEMBER("DUPLICATED_MEMBER", "이미 존재하는 회원", HttpStatus.BAD_REQUEST),
    VERIFIED_MEMBER("VERIFIED_MEMBER", "이미 인증이 완료된 회원", HttpStatus.BAD_REQUEST),
    INVALID_AUTH("INVALID_AUTH", "유효하지 않은 인증", HttpStatus.BAD_REQUEST),
    EXPIRED_AUTH("EXPIRED_AUTH", "만료된 인증", HttpStatus.BAD_REQUEST),
    NOT_COMPLETE_AUTH("NOT_COMPLETE_AUTH","인증이 완료되지 않음", HttpStatus.BAD_REQUEST),
    NOT_FOUND_CLUB("NOT_COMPLETE_CLUB","존재하지 않는 모임입니다.", HttpStatus.BAD_REQUEST),
    INVALID_VERIF_CODE("NOT_COMPLETE_AUTH","유효하지않은 인증번호", HttpStatus.BAD_REQUEST);



    // 추후에 추가될 다른 업로드 타입들

    private final String statusCode;
    private final String statusMessage;

    // 추가: HttpStatus 열거 값을 저장할 필드
    private final HttpStatus httpStatus;

    CustomExceptionCode(String statusCode, String statusMessage, HttpStatus httpStatus) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.httpStatus = httpStatus; // HttpStatus를 생성자로 받아서 저장
    }
    public String getStatusCode() {
        return statusCode;
    }
    public String getStatusMessage() {
        return statusMessage;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}