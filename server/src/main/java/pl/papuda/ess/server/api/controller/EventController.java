package pl.papuda.ess.server.api.controller;

import java.security.Principal;
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
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.EventCreationRequest;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.EventService;

@RestController
@RequestMapping("/api/v1/staff/event")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventCreationRequest event, Principal principal) {
        Optional<User> creator = userRepository.findByUsername(principal.getName());
        if (creator.isEmpty()) {
            return ErrorResponse.generate("Authenticated as non-existing user; cannot create event",
                    HttpStatus.UNAUTHORIZED);
        }
        return eventService.createEvent(creator.get(), event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventCreationRequest event) {
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isEmpty()) {
            return ErrorResponse.generate("Event not found", HttpStatus.NOT_FOUND);
        }
        return eventService.updateEvent(eventData.get(), event);
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
