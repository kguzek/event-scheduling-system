package pl.papuda.ess.server.tasks;

import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.service.EmailService;

class EmailNotificationStrategy implements NotificationStrategy {
    private final EmailService emailService;

    public EmailNotificationStrategy(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendNotification(User user, String title, String message) {
        emailService.sendEmail(user.getEmail(), title, message);
    }
}
