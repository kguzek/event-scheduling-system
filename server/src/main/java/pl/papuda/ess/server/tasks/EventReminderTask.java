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
        System.out.printf("Running event reminder CRON job at %s%n", roundedTime);
        System.out.printf("Current exact time: %s%n", LocalDateTime.now());
        List<Event> allEvents = eventRepository.findAll();
        for (Event event : allEvents) {
            System.out.printf("Event %s reminder time: %s%n", event.getId(), event.getReminderTime());
        }
        List<Event> events = eventRepository.findAllByReminderTime(roundedTime);
        for (Event event : events) {
            notifyEventAttendees(event);
        }
    }

    private long getTimeUntilEvent(Event event) {
        LocalDateTime eventLocalDateTime = event.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duration = Duration.between(eventLocalDateTime, LocalDateTime.now());
        return Math.round(duration.getSeconds() / 60.0);
    }

    private void notifyEventAttendees(Event event) {
        String title = event.getTitle();
        String message = String.format("Event %s is starting in %d - don't miss out!", title, getTimeUntilEvent(event));
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
