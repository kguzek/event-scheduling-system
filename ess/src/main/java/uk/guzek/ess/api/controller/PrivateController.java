package uk.guzek.ess.api.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.guzek.ess.api.model.ErrorResponse;
import uk.guzek.ess.api.model.Event;
import uk.guzek.ess.api.model.User;
import uk.guzek.ess.api.repo.EventRepository;
import uk.guzek.ess.api.repo.UserRepository;


@RestController
@RequestMapping("/api/v1/private")
public class PrivateController {
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private UserRepository userRepository;

  private ResponseEntity<?> setAttendanceStatus(Long eventId, Principal principal, boolean attending) {
    Optional<Event> eventData = eventRepository.findById(eventId);
    if (eventData.isEmpty()) {
      return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
    }
    Optional<User> userData = userRepository.findByUsername(principal.getName());
    if (userData.isEmpty()) {
      return ErrorResponse.generate("Authenticated as non-existing user; cannot create event",
          HttpStatus.UNAUTHORIZED);
    }
    Event event = eventData.get();
    User user = userData.get();
    Set<User> attendees = event.getAttendees();
    // Set<Event> attendedEvents = user.getAttendedEvents();
    if (attendees.contains(user) == attending) {
      return ErrorResponse.generate("The attendance status is already at that value", HttpStatus.CONFLICT);
    }
    if (attending) {
      attendees.add(user);
      // attendedEvents.add(event);
    } else {
      attendees.remove(user);
      // attendedEvents.remove(event);
    }
    event.setAttendees(attendees);
    // user.setAttendedEvents(attendedEvents);
    // eventRepository.save(event);
    // return ResponseEntity.ok(userRepository.save(user));
    return ResponseEntity.ok(eventRepository.save(event));
  }

  @PostMapping("/attendance/{id}")
  public ResponseEntity<?> declareAttendance(@PathVariable Long id, Principal principal) {
    return setAttendanceStatus(id, principal, true);
  }

  @DeleteMapping("/attendance/{id}")
  public ResponseEntity<?> withdrawAttendance(@PathVariable Long id, Principal principal) {
    return setAttendanceStatus(id, principal, false);
  }
}