package com.example.gazamung.fcmSend;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface FcmService {

    int sendMessageTo(FcmSendDto fcmSendDto) throws IOException;


}