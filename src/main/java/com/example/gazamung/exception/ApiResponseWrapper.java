package com.example.gazamung.exception;

public class ApiResponseWrapper<T> {
    private int resultCode;
    private String resultMsg;
    private T result;

    // Builder 클래스를 사용하여 객체를 더 쉽게 생성할 수 있도록 합니다
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private int resultCode;
        private String resultMsg;
        private T result;

        public Builder<T> resultCode(int resultCode) {
            this.resultCode = resultCode;
            return this;
        }

        public Builder<T> resultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
            return this;
        }

        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        public ApiResponseWrapper<T> build() {
            ApiResponseWrapper<T> response = new ApiResponseWrapper<>();
            response.resultCode = this.resultCode;
            response.resultMsg = this.resultMsg;
            response.result = this.result;
            return response;
        }
    }

    // Getter 및 Setter 메소드는 필요에 따라 추가합니다.
}