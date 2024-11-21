package pl.papuda.ess.server.api.service;

import java.io.InvalidObjectException;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.EventStatus;
import pl.papuda.ess.server.api.model.Frequency;
import pl.papuda.ess.server.api.model.Location;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.EventCreationRequest;
import pl.papuda.ess.server.api.model.body.websocket.StompResponse;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.UserRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    private Event mergeEvents(Event oldEvent, EventCreationRequest newEvent) throws InvalidObjectException {
        String title = newEvent.getTitle();
        String organiser = newEvent.getOrganiserName();
        Date startTime = newEvent.getStartTime();
        Date endTime = newEvent.getEndTime();
        Date reminderTime = newEvent.getReminderTime();
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

    private User getUserFromPrincipal(Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    private StompResponse<String> ensureUserIsStaff(User user) {
        if (user != null && user.getRole().equals(pl.papuda.ess.server.api.model.Role.STAFF)) {
            return null;
        }
        return new StompResponse<String>(false, "You are not authorised to perform this action");
    }

    public StompResponse<?> createEvent(Principal principal, EventCreationRequest event) {
        User creator = getUserFromPrincipal(principal);
        StompResponse<String> staffResponse = ensureUserIsStaff(creator);
        if (staffResponse != null)
            return staffResponse;
        Event eventObj;
        try {
            eventObj = mergeEvents(new Event(), event);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
            return new StompResponse<String>(false, e.getMessage());
        }
        eventObj.setCreator(creator);
        eventObj.setAttendees(Collections.emptySet());
        eventObj.setExpenses(Collections.emptyList());
        Event savedEvent = eventRepository.save(eventObj);
        return new StompResponse<Event>(true, savedEvent);
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
            e.printStackTrace();
            return new StompResponse<String>(false, e.getMessage());
        }
        Event savedEvent = eventRepository.save(eventObj);
        return new StompResponse<Event>(true, savedEvent);
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
