package pl.papuda.ess.client.eventObserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.services.EventService;

public abstract class EventModificationObserver extends EventGenericObserver {

    abstract void handleEventModification(Event event);
    private final EventService eventService;

    public EventModificationObserver(EventService eventService) {
        this.eventService = eventService;
    }

    EventService getEventService() {
        return eventService;
    }

    @Override
    void handleMessage(Object messageBody) {
        ObjectMapper mapper = new ObjectMapper();
        Event createdEvent = mapper.convertValue(messageBody, Event.class);
        handleEventModification(createdEvent);
    }
}
