package pl.papuda.ess.server.api.controller;

import java.io.InvalidObjectException;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.EventStatus;
import pl.papuda.ess.server.api.model.Frequency;
import pl.papuda.ess.server.api.model.Location;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.EventCreationRequest;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.UserRepository;

@RestController
@RequestMapping("/api/v1/staff/event")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

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

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventCreationRequest event, Principal principal) {
        Optional<User> creator = userRepository.findByUsername(principal.getName());
        if (creator.isEmpty()) {
            return ErrorResponse.generate("Authenticated as non-existing user; cannot create event",
                    HttpStatus.UNAUTHORIZED);
        }
        Event savedEvent;
        try {
            Event eventObj = mergeEvents(new Event(), event);
            eventObj.setCreator(creator.get());
            eventObj.setAttendees(Collections.emptySet());
            eventObj.setExpenses(Collections.emptyList());
            savedEvent = eventRepository.save(eventObj);
        } catch (InvalidObjectException e) {
            return ErrorResponse.generate(e.getMessage());
        } catch (Exception e) {
            return ErrorResponse.generate(e.getMessage());
        }
        return ResponseEntity.ok(savedEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventCreationRequest event) {
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isEmpty()) {
            return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
        }
        Event eventObj;
        try {
            eventObj = mergeEvents(eventData.get(), event);
        } catch (InvalidObjectException e) {
            return ErrorResponse.generate(e.getMessage());
        }
        return ResponseEntity.ok(eventRepository.save(eventObj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id, Principal principal) {
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isPresent()) {
            Event event = eventData.get();
            if (!event.getCreator().getUsername().equals(principal.getName())) {
                return ErrorResponse.generate("You cannot delete an event that was not created by you",
                        HttpStatus.FORBIDDEN);
            }
            eventRepository.delete(event);
        }
        return ResponseEntity.noContent().build();
    }

}
