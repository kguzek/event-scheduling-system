package pl.papuda.ess.server.api.service;

import java.io.InvalidObjectException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.papuda.ess.server.api.model.*;
import pl.papuda.ess.server.api.model.body.EventCreationRequest;
import pl.papuda.ess.server.api.model.body.websocket.StompResponse;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Event mergeEvents(Event oldEvent, EventCreationRequest newEvent) throws InvalidObjectException {
        String title = newEvent.getTitle();
        String organiser = newEvent.getOrganiserName();
        ZonedDateTime startTime = newEvent.getStartTime();
        ZonedDateTime endTime = newEvent.getEndTime();
        ZonedDateTime reminderTime = newEvent.getReminderTime();
        String feedbackMessage = newEvent.getFeedbackMessage();
        Frequency frequency = newEvent.getFrequency();
        Location location = newEvent.getLocation();
        EventStatus status = newEvent.getStatus() == null ? EventStatus.PLANNED : newEvent.getStatus();
        if (title == null || organiser == null || startTime == null || location == null) {
            throw new InvalidObjectException("Request payload event structure is incomplete");
        }
        if (endTime != null && startTime.compareTo(endTime) >= 0) {
            throw new InvalidObjectException("Event end time must be `null` or after the start time");
        }
        oldEvent.setTitle(title);
        oldEvent.setOrganiserName(organiser);
        oldEvent.setStartTime(startTime);
        oldEvent.setEndTime(endTime);
        oldEvent.setFrequency(frequency);
        oldEvent.setLocation(location);
        oldEvent.setStatus(status);
        oldEvent.setReminderTime(reminderTime);
        oldEvent.setFeedbackMessage(feedbackMessage);
        return oldEvent;
    }

    public User getUserFromPrincipal(Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        return user.orElse(null);
    }

    public StompResponse<String> ensureUserIsStaff(User user) {
        String errorMessage = null;
        if (user == null) {
            errorMessage = "You must be logged in to perform that action.";
        } else {
            Role role = user.getRole();
            if (!role.equals(Role.STAFF)) {
                errorMessage = "Only staff members may perform that action. You are: " + role;
            }
        }
        if (errorMessage == null) return null;
        return new StompResponse<>(false, errorMessage);
    }

    public StompResponse<Event> createEvent(Event eventObj, User creator) {
        eventObj.setCreator(creator);
        eventObj.setAttendees(Collections.emptySet());
        eventObj.setExpenses(Collections.emptyList());
        Event savedEvent = eventRepository.save(eventObj);
        return new StompResponse<>(true, savedEvent);
    }

    @Transactional
    public StompResponse<?> updateEvent(Principal principal, Event previousEvent, EventCreationRequest newEvent) {
        StompResponse<String> staffResponse = ensureUserIsStaff(getUserFromPrincipal(principal));
        if (staffResponse != null)
            return staffResponse;
        Event eventObj;
        try {
            eventObj = mergeEvents(previousEvent, newEvent);
        } catch (InvalidObjectException e) {
            System.err.println("Invalid event structure: " + e.getMessage());
            return new StompResponse<>(false, e.getMessage());
        }
        Event savedEvent = eventRepository.save(eventObj);
        return new StompResponse<>(true, savedEvent);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
