package pl.papuda.ess.client.eventObserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.papuda.ess.client.interfaces.Observer;
import pl.papuda.ess.client.services.EventService;

public class EventDeletionObserver implements Observer {

    private final EventService eventService;

    public EventDeletionObserver(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void update(Object messageBody) {
        ObjectMapper mapper = new ObjectMapper();
        Long eventId = mapper.convertValue(messageBody, Long.class);
        eventService.removeEvent(eventId);
    }
}
