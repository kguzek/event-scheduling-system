package pl.papuda.ess.client.eventObserver;

import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.services.EventService;

public class EventUpdateObserver extends EventModificationObserver {

    public EventUpdateObserver(EventService eventService) {
        super(eventService);
    }

    @Override
    void handleEventModification(Event event) {
        getEventService().addEvent(event);
    }
}
