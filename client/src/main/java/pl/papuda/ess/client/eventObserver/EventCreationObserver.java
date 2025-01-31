package pl.papuda.ess.client.eventObserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.papuda.ess.client.interfaces.Observer;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.services.EventService;

public class EventCreationObserver implements Observer {

    private final EventService eventService;

    public EventCreationObserver(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void update(Object messageBody) {
        ObjectMapper mapper = new ObjectMapper();
        Event createdEvent = mapper.convertValue(messageBody, Event.class);
        eventService.addEvent(createdEvent);
    }
}
