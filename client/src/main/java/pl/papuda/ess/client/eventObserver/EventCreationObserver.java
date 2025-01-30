package pl.papuda.ess.client.eventObserver;

import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.services.EventService;

public class EventCreationObserver extends EventModificationObserver {

    public EventCreationObserver(EventService eventService) {
        super(eventService);
    }

    @Override
    void handleEventModification(Event event) {
        getEventService().addEvent(event);
    }
}
