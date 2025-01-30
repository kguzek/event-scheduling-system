package pl.papuda.ess.client.eventObserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.papuda.ess.client.services.EventService;

public class EventDeletionObserver extends EventGenericObserver {

    private final EventService eventService;

    public EventDeletionObserver(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    void handleMessage(Object messageBody) {
        ObjectMapper mapper = new ObjectMapper();
        Long eventId = mapper.convertValue(messageBody, Long.class);
        eventService.removeEvent(eventId);
    }
}
