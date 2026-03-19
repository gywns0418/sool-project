package com.example.sool.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAuthCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[SOOLNOTE] 이메일 인증번호 안내");
        message.setText("인증번호는 [" + code + "] 입니다. 3분 이내에 입력해주세요.");
        mailSender.send(message);
    }
}