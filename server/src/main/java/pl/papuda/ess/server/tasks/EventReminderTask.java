package pl.papuda.ess.server.tasks;

import org.springframework.stereotype.Component;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.service.EmailService;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Component
public class EventReminderTask {

    private final EventRepository eventRepository;
    private final NotificationStrategy emailNotificationStrategy;
    private final NotificationStrategy popupNotificationStrategy;

    public EventReminderTask(EventRepository eventRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.emailNotificationStrategy = new EmailNotificationStrategy(emailService);
        this.popupNotificationStrategy = new PopupNotificationStrategy();
    }

    /** Returns the current date truncated to the minute start, in UTC */
    private ZonedDateTime currentMinuteStart() {
        LocalDateTime nowTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return ZonedDateTime.of(nowTruncated, ZoneId.systemDefault());
    }

    public void runReminderCheck() {
        ZonedDateTime roundedTime = currentMinuteStart();
        List<Event> events = eventRepository.findAllByReminderTime(roundedTime);
        if (events.isEmpty()) {
            return;
        }
        System.out.printf("CRON job at %s%n found %d candidate events", roundedTime, events.size());
        System.out.printf("Current exact time: %s%n", LocalDateTime.now());
        for (Event event : events) {
            notifyEventAttendees(event);
        }
    }

    private String getTimeUntilEvent(Event event) {
        LocalDateTime eventLocalDateTime = event.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duration = Duration.between(LocalDateTime.now(), eventLocalDateTime);
        int minutesUntilEvent = (int) Math.round(duration.getSeconds() / 60.0);
        return switch (minutesUntilEvent) {
            case 0 -> "now";
            case 1 -> "in 1 minute";
            default -> String.format("in %d minutes", minutesUntilEvent);
        };
    }

    private void notifyEventAttendees(Event event) {
        String title = event.getTitle();
        String message = String.format("Event %s is starting %s – don't miss out! Start time: %s", title, getTimeUntilEvent(event), event.getStartTime());
        Set<User> attendees = event.getAttendees();
        System.out.printf("Notifying %d attendees of event with id %d%n", attendees.size(), event.getId());
        for (User user : attendees) {
            switch (user.getPreferredNotificationMethod()) {
                case EMAIL:
                    emailNotificationStrategy.sendNotification(user, title, message);
                    break;
                case POPUP:
                    popupNotificationStrategy.sendNotification(user, title, message);
                    break;
                case POPUP_AND_EMAIL:
                    popupNotificationStrategy.sendNotification(user, title, message);
                    emailNotificationStrategy.sendNotification(user, title, message);
                    break;
                default:
                    System.err.println("Unsupported notification method: " + user.getPreferredNotificationMethod());
                    break;
            }
        }
    }
}
