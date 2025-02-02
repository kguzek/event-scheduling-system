package pl.papuda.ess.server.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.common.strategy.EmailNotificationStrategy;
import pl.papuda.ess.server.common.strategy.PopupNotificationStrategy;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final EventRepository eventRepository;
    private final EmailNotificationStrategy emailNotificationStrategy;
    private final PopupNotificationStrategy popupNotificationStrategy;

    /** Returns the current date truncated to the minute start, in UTC */
    private ZonedDateTime currentMinuteStart() {
        LocalDateTime nowTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return ZonedDateTime.of(nowTruncated, ZoneId.systemDefault());
    }

    @Scheduled(cron = "${EVENT_REMINDER_CRON_RATE}")
    protected void runReminderCheck() {
        ZonedDateTime roundedTime = currentMinuteStart();
        List<Event> events = eventRepository.findAllByReminderTime(roundedTime);
        if (events.isEmpty()) {
            return;
        }
        System.out.printf("CRON job reminding about %d events at %s%n", events.size(), roundedTime);
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
        String message = String.format("Event %s is starting %s – don't miss out! Start time: %s", event.getTitle(), getTimeUntilEvent(event), event.getStartTime());
        Set<User> attendees = event.getAttendees();
        System.out.printf("Notifying %d attendees of event with id %d%n", attendees.size(), event.getId());
        for (User user : attendees) {
            switch (user.getPreferredNotificationMethod()) {
                case EMAIL:
                    emailNotificationStrategy.sendNotification(event, message);
                    break;
                case POPUP:
                    popupNotificationStrategy.sendNotification(event, message);
                    break;
                case POPUP_AND_EMAIL:
                    popupNotificationStrategy.sendNotification(event, message);
                    emailNotificationStrategy.sendNotification(event, message);
                    break;
                default:
                    System.err.println("Unsupported notification method: " + user.getPreferredNotificationMethod());
                    break;
            }
        }
    }
}
