package com.servicecenter.service_center_management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendOtp_Success() {
        String toEmail = "test@example.com";
        String otpCode = "123456";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendOtp(toEmail, otpCode);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Account Verification OTP", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(otpCode));
        assertTrue(sentMessage.getText().contains("Valid for 10 minutes"));
    }

    @Test
    void testSendEmployeePassword_Success() {
        String toEmail = "employee@example.com";
        String password = "TempPass123";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmployeePassword(toEmail, password);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Employee Account Created", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(toEmail));
        assertTrue(sentMessage.getText().contains(password));
    }

    @Test
    void testSendCredentials_Success() {
        String toEmail = "user@example.com";
        String password = "SecurePass456";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendCredentials(toEmail, password);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Your Account Credentials", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(password));
        assertTrue(sentMessage.getText().contains("account has been created"));
    }

    @Test
    void testSendOtp_VerifyMessageFormat() {
        String toEmail = "verify@example.com";
        String otpCode = "987654";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendOtp(toEmail, otpCode);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        String expectedText = "Your OTP: " + otpCode + "\nValid for 10 minutes.";
        assertEquals(expectedText, sentMessage.getText());
    }

    @Test
    void testSendCredentials_VerifyAllFields() {
        String toEmail = "admin@example.com";
        String password = "Admin123";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendCredentials(toEmail, password);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals(1, sentMessage.getTo().length);
        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertNotNull(sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
    }
}
