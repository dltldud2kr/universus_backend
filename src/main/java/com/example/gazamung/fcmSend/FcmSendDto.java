package com.example.gazamung.fcmSend;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
@AllArgsConstructor

public class FcmSendDto {
    private String token;
    private String title;
    private String body;
    private String target;
    private String data;


}