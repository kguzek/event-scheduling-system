package pl.papuda.ess.server.api.service;

import java.io.InvalidObjectException;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.EventStatus;
import pl.papuda.ess.server.api.model.Frequency;
import pl.papuda.ess.server.api.model.Location;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.EventCreationRequest;
import pl.papuda.ess.server.api.repo.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

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

    public ResponseEntity<?> createEvent(User creator, EventCreationRequest event) {
        Event savedEvent;
        try {
            Event eventObj = mergeEvents(new Event(), event);
            eventObj.setCreator(creator);
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

	public ResponseEntity<?> updateEvent(Event savedEvent, EventCreationRequest event) {
        Event eventObj;
        try {
            eventObj = mergeEvents(savedEvent, event);
        } catch (InvalidObjectException e) {
            return ErrorResponse.generate(e.getMessage());
        }
        return ResponseEntity.ok(eventRepository.save(eventObj));
	}
	
}
