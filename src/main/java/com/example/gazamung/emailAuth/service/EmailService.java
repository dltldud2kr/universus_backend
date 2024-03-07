package com.example.gazamung.emailAuth.service;

public interface EmailService {
    String sendEmailVerification(String to)throws Exception;
}
