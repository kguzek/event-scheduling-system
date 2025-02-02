package pl.papuda.ess.server.common.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.service.EmailService;

@Service
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy {

    private final EmailService emailService;

    public void sendNotification(Event event, String message) {
        for (User attendee : event.getAttendees()) {
            emailService.sendEmail(attendee.getEmail(), event.getTitle(), message);
        }
    }
}
