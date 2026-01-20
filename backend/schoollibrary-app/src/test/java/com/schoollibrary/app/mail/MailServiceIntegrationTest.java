package com.schoollibrary.app.mail;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Integration Test for Mail Service (Gmail SMTP).
 * 
 * This test verifies that the mail configuration is correct and can connect to Gmail SMTP.
 * 
 * IMPORTANT: This test requires valid Gmail credentials to be set as environment variables:
 * - MAIL_USERNAME: Your Gmail address
 * - MAIL_PASSWORD: Your Gmail App Password (not regular password)
 * - MAIL_FROM: The sender address (optional, defaults to noreply@schoollibrary.local)
 * 
 * To run this test:
 * 1. Create a Gmail App Password: Google Account → Security → 2-Step Verification → App passwords
 * 2. Set environment variables:
 *    export MAIL_USERNAME=your-email@gmail.com
 *    export MAIL_PASSWORD=your-app-password
 *    export MAIL_FROM=noreply@schoollibrary.local
 * 3. Run: mvn test -Dtest=MailServiceIntegrationTest
 * 
 * @author Backend Team
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Mail Service Integration Tests")
class MailServiceIntegrationTest {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@schoollibrary.local}")
    private String mailFrom;

    @Test
    @DisplayName("JavaMailSender should be autowired and configured")
    void testMailSenderIsConfigured() {
        assertThat(mailSender).isNotNull();
        assertThat(mailFrom).isNotEmpty();
    }

    @Test
    @DisplayName("Should create and prepare a verification email without errors")
    void testCreateVerificationEmail() {
        assertDoesNotThrow(() -> {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(mailFrom);
            helper.setTo("test@example.com");
            helper.setSubject("School Library - Email Verification");
            helper.setText(buildVerificationEmailBody("John Doe", "https://localhost:8080/verify?token=test123"), true);
            
            assertThat(message).isNotNull();
            assertThat(message.getSubject()).isEqualTo("School Library - Email Verification");
        });
    }

    /**
     * CAUTION: This test actually sends a simple email!
     * 
     * This test verifies that Gmail SMTP is correctly configured and can send emails.
     * Uncomment @Disabled and set your test email address before running.
     * 
     * To run only this test:
     * mvn test -pl schoollibrary-app -Dtest=MailServiceIntegrationTest#testSendSimpleTestEmail
     */
    @Test
    @Disabled("Uncomment to run the email sending test. Make sure to set a valid recipient email.")
    @DisplayName("Should send a simple test email via Gmail SMTP to verify configuration")
    void testSendSimpleTestEmail() {
        String recipientEmail = "uwe.testmail.server@gmail.com"; 
        
        assertDoesNotThrow(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(recipientEmail);
            message.setSubject("School Library - SMTP Test Email");
            message.setText("""
                Hello!
                
                This is a test email from the School Library application.
                
                If you received this email, it means that:
                ✅ Gmail SMTP is correctly configured
                ✅ Authentication with Gmail is working
                ✅ Email sending functionality is operational
                
                Timestamp: %s
                
                Best regards,
                School Library System
                """.formatted(java.time.LocalDateTime.now()));
            
            mailSender.send(message);
            
            System.out.println("✅ Simple test email sent successfully to: " + recipientEmail);
            System.out.println("📧 Check your inbox!");
        });
    }

    /**
     * CAUTION: This test actually sends an email!
     * 
     * Uncomment and modify the recipient email before running.
     * Make sure you have valid Gmail credentials set as environment variables.
     */
    // @Test
    // @DisplayName("Should send a real verification email via Gmail SMTP")
    // void testSendVerificationEmail() {
    //     String recipientEmail = "YOUR_TEST_EMAIL@example.com"; // CHANGE THIS!
    //     String userName = "Test User";
    //     String verificationToken = "test-token-" + System.currentTimeMillis();
    //     String verificationLink = "http://localhost:8080/api/v1/auth/verify?token=" + verificationToken;
    //     
    //     assertDoesNotThrow(() -> {
    //         MimeMessage message = mailSender.createMimeMessage();
    //         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    //         
    //         helper.setFrom(mailFrom);
    //         helper.setTo(recipientEmail);
    //         helper.setSubject("School Library - Email Verification");
    //         helper.setText(buildVerificationEmailBody(userName, verificationLink), true);
    //         
    //         mailSender.send(message);
    //         
    //         System.out.println("✅ Verification email sent successfully to: " + recipientEmail);
    //         System.out.println("📧 Check your inbox!");
    //     });
    // }

    /**
     * Build HTML email body for verification email.
     * 
     * @param userName the name of the user
     * @param verificationLink the verification link
     * @return HTML email body
     */
    private String buildVerificationEmailBody(String userName, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; 
                              color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📚 School Library</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s!</h2>
                        <p>Thank you for registering with the School Library system.</p>
                        <p>Please verify your email address by clicking the button below:</p>
                        <div style="text-align: center;">
                            <a href="%s" class="button">Verify Email Address</a>
                        </div>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #4CAF50;">%s</p>
                        <p><strong>This link will expire in 24 hours.</strong></p>
                        <p>If you did not create an account, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 School Library. All rights reserved.</p>
                        <p>This is an automated message, please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, verificationLink, verificationLink);
    }
}
