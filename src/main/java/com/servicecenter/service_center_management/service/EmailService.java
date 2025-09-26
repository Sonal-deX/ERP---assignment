package com.servicecenter.service_center_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendOtp(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Verification OTP");
        message.setText("Your OTP: " + otpCode + "\nValid for 10 minutes.");
        mailSender.send(message);
    }
    
    public void sendEmployeePassword(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Employee Account Created");
        message.setText("Your login credentials:\nEmail: " + toEmail + "\nPassword: " + password);
        mailSender.send(message);
    }
}