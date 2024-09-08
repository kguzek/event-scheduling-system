package uk.guzek.ess.api.controller;

import java.io.InvalidObjectException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.guzek.ess.api.model.ErrorResponse;
import uk.guzek.ess.api.model.Event;
import uk.guzek.ess.api.model.Frequency;
import uk.guzek.ess.api.model.Location;
import uk.guzek.ess.api.model.User;
import uk.guzek.ess.api.model.body.EventCreationRequest;
import uk.guzek.ess.api.repo.EventRepository;
import uk.guzek.ess.api.repo.UserRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {
  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<Event>> getAllEvents() {
    return ResponseEntity.ok(eventRepository.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getEvent(@PathVariable Long id) {
    Optional<Event> eventData = eventRepository.findById(id);
    if (eventData.isEmpty()) {
      return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(eventData.get());
  }

  private Event mergeEvents(Event oldEvent, EventCreationRequest newEvent) throws InvalidObjectException {
    String title = newEvent.getTitle();
    String organiser = newEvent.getOrganiserName();
    Date datetime = newEvent.getDatetime();
    Frequency frequency = newEvent.getFrequency();
    Location location = newEvent.getLocation();
    if (title == null || organiser == null || datetime == null || location == null) {
      throw new InvalidObjectException("Request payload event structure is incomplete");
    }
    oldEvent.setTitle(title);
    oldEvent.setOrganiserName(organiser);
    oldEvent.setDatetime(datetime);
    oldEvent.setFrequency(frequency);
    oldEvent.setLocation(location);
    return oldEvent;
  }

  @PostMapping
  public ResponseEntity<?> createEvent(@RequestBody EventCreationRequest event, Principal principal) {
    Optional<User> creator = userRepository.findByUsername(principal.getName());
    if (creator.isEmpty()) {
      return ErrorResponse.generate("Authenticated as non-existing user. Cannot create event.",
          HttpStatus.UNAUTHORIZED);
    }
    Event savedEvent;
    try {
      Event eventObj = mergeEvents(new Event(), event);
      eventObj.setCreator(creator.get());
      savedEvent = eventRepository.save(eventObj);
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
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
    eventRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
