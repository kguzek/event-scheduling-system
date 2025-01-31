package pl.papuda.ess.server.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${API_URL}")
    private String API_URL;

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String recipientAddress, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            helper.setTo(recipientAddress);
            helper.setSubject(subject + " | Event Scheduling System");
            helper.setText(content.replace("{API_URL}", API_URL), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println(e);
        }
    }
}
