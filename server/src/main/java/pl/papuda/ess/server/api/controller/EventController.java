package pl.papuda.ess.server.api.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.EventCreationRequest;
import pl.papuda.ess.server.api.model.body.websocket.StompResponse;
import pl.papuda.ess.server.api.repo.EventRepository;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.EventService;

@Controller
@MessageMapping("/event")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventService eventService;

    @MessageMapping("/create")
    @SendTo("/topic/events/created")
    public StompResponse<?> createEvent(@Payload EventCreationRequest event, Principal principal) {
        System.out.println("Creating event");
        Optional<User> creator = userRepository.findByUsername(principal.getName());
        if (creator.isEmpty()) {
            return new StompResponse<String>(false, "Authenticated as non-existing user; cannot create event");
        }
        User user = creator.get();
        return eventService.createEvent(user, event);
    }

    @MessageMapping("/update/{id}")
    @SendTo("/topic/events/updated")
    public StompResponse<?> updateEvent(@DestinationVariable Long id, @Payload EventCreationRequest event) {
        System.out.println("Updating event with id: " + id + ", event start time: " + event.getStartTime() + " = "
                + event.getStartTime().getTime());
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isEmpty()) {
            return new StompResponse<String>(false, "Event not found");
        }
        Event value = eventData.get();
        return eventService.updateEvent(value, event);
    }

    @MessageMapping("/delete/{id}")
    @SendTo("/topic/events/deleted")
    public StompResponse<?> deleteEvent(@DestinationVariable Long id, Principal principal) {
        System.out.println("Deleting event with id: " + id);
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isPresent()) {
            Event event = eventData.get();
            if (!event.getCreator().getUsername().equals(principal.getName())) {
                return new StompResponse<String>(false, "You cannot delete an event that was not created by you");
            }
            eventService.deleteEvent(event);
        }
        return new StompResponse<Long>(true, id);
    }
}
