package pl.papuda.ess.server.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${API_URL}")
    private String API_URL;

    private String substitutePlaceholders(String content) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        return content.replace("{API_URL}", API_URL).replace("{CURRENT_YEAR}", currentYear);
    }

    public boolean sendEmail(String recipientAddress, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String substitutedContent = substitutePlaceholders(content);
        try {
            helper.setTo(recipientAddress);
            helper.setSubject(subject + " | Event Scheduling System");
            helper.setText(substitutedContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Email message send failed: " + e);
            return false;
        }
        System.out.printf("Email message to %s with subject %s sent successfully%n", recipientAddress, subject);
        return true;
    }
}
