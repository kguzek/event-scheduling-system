package pl.papuda.ess.server.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${API_URL}")
    private String API_URL;

    @Value("${EMAIL_ADDRESS}")
    String emailAddress;

    @Value("${EMAIL_PASSWORD}")
    String emailPassword;

    @Value("${EMAIL_SMTP_HOST}")
    String emailHost;

    @Value("${EMAIL_SMTP_PORT}")
    int emailPort;

    private JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);
        mailSender.setUsername(emailAddress);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        // props.put("mail.debug", "true");
        return mailSender;
    }

    private String substitutePlaceholders(String content) {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        return content.replace("{API_URL}", API_URL).replace("{CURRENT_YEAR}", currentYear);
    }

    public boolean sendEmail(String recipientAddress, String subject, String content) {
        JavaMailSender mailSender = getMailSender();
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
        return true;
    }
}
