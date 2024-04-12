package com.example.string_boot_4.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    public void sendTemporaryPassword(String to, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("임시 비밀번호 발급 안내");
        message.setText("안녕하세요, 임시 비밀번호를 안내해드립니다. 임시 비밀번호는 " + temporaryPassword + "입니다.");
        emailSender.send(message);
    }
}
