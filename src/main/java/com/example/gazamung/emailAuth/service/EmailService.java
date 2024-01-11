package com.example.gazamung.emailAuth.service;

public interface EmailService {
    void sendEmailVerification(String to)throws Exception;
}
