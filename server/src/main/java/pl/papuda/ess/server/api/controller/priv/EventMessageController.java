package pl.papuda.ess.server.api.controller.priv;

import java.io.InvalidObjectException;
import java.security.Principal;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
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
import pl.papuda.ess.server.api.service.EventService;

@Controller
@MessageMapping("/event")
@RequiredArgsConstructor
public class EventMessageController {

    private final EventRepository eventRepository;
    private final EventService eventService;

    @MessageMapping("/create")
    @SendTo("/topic/events/created")
    public StompResponse<?> createEvent(@Payload EventCreationRequest event, Principal principal) {
        System.out.println("Creating event");
        User creator = eventService.getUserFromPrincipal(principal);
        StompResponse<String> staffResponse = eventService.ensureUserIsStaff(creator);
        if (staffResponse != null) {
            return staffResponse;
        }
        Event eventObj;
        try {
            eventObj = eventService.mergeEvents(new Event(), event);
        } catch (InvalidObjectException e) {
            System.err.println("Invalid object at event creation: " + e.getMessage());
            return new StompResponse<>(false, e.getMessage());
        }
        return eventService.createEvent(eventObj, creator);
    }

    @MessageMapping("/update/{id}")
    @SendTo("/topic/events/updated")
    public StompResponse<?> updateEvent(@DestinationVariable Long id, @Payload EventCreationRequest event,
            Principal principal) {
        System.out.println("Updating event with id: " + id + ", event start time: " + event.getStartTime());
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isEmpty()) {
            return new StompResponse<>(false, "Event not found");
        }
        Event value = eventData.get();
        return eventService.updateEvent(principal, value, event);
    }

    @MessageMapping("/delete/{id}")
    @SendTo("/topic/events/deleted")
    public StompResponse<?> deleteEvent(@DestinationVariable Long id, Principal principal) {
        System.out.println("Deleting event with id: " + id);
        Optional<Event> eventData = eventRepository.findById(id);
        if (eventData.isPresent()) {
            Event event = eventData.get();
            if (!event.getCreator().getUsername().equals(principal.getName())) {
                return new StompResponse<>(false, "You cannot delete an event that was not created by you");
            }
            eventService.deleteEvent(event);
        }
        return new StompResponse<>(true, id);
    }
}
