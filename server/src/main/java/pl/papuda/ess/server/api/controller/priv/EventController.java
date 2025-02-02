package pl.papuda.ess.server.api.controller.priv;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.papuda.ess.server.api.model.*;
import pl.papuda.ess.server.api.model.body.EmailReminderRequest;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.EmailService;
import pl.papuda.ess.server.common.RestResponse;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/private/event")
public class EventController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EventController(EventRepository eventRepository, UserRepository userRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private ResponseEntity<?> setAttendanceStatus(Long eventId, Principal principal, boolean attending) {
        Optional<Event> eventData = eventRepository.findById(eventId);
        if (eventData.isEmpty()) {
            return RestResponse.notFound("Event");
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            return RestResponse.generate("Authenticated as non-existing user; cannot create event",
                    HttpStatus.UNAUTHORIZED);
        }
        Event event = eventData.get();
        User user = userData.get();
        Set<User> attendees = event.getAttendees();
        // Set<Event> attendedEvents = user.getAttendedEvents();
        if (attendees.contains(user) == attending) {
            return RestResponse.generate("The attendance status is already at that value", HttpStatus.CONFLICT);
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


    private ResponseEntity<RestResponse> getEmailResultResponse(boolean success) {
        return success
                ? RestResponse.ok("Email message sent successfully")
                : RestResponse.generate("An unknown error occurred while sending the email message", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/reminder/email")
    public ResponseEntity<RestResponse> sendReminderEmail(@RequestBody EmailReminderRequest request) {
        boolean success = emailService.sendEmail(request.getRecipient(), request.getSubject(), request.getBody());
        return getEmailResultResponse(success);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isEmpty()) {
            return RestResponse.notFound("Event");
        }
        return ResponseEntity.ok(eventData.get());
    }

    @PostMapping("/{id}/attendee")
    public ResponseEntity<?> declareAttendance(@PathVariable Long id, Principal principal) {
        return setAttendanceStatus(id, principal, true);
    }

    @DeleteMapping("/{id}/attendee")
    public ResponseEntity<?> withdrawAttendance(@PathVariable Long id, Principal principal) {
        return setAttendanceStatus(id, principal, false);
    }


}
