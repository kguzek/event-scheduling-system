package pl.papuda.ess.client.services;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import pl.papuda.ess.client.components.home.EventsList;
import pl.papuda.ess.client.components.home.calendar.CalendarCustom;
import pl.papuda.ess.client.eventObserver.EventCreationObserver;
import pl.papuda.ess.client.eventObserver.EventDeletionObserver;
import pl.papuda.ess.client.eventObserver.EventObservable;
import pl.papuda.ess.client.eventObserver.EventUpdateObserver;
import pl.papuda.ess.client.pages.HomePage;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.interfaces.Observable;
import pl.papuda.ess.client.interfaces.Observer;
import pl.papuda.ess.client.model.User;
import pl.papuda.ess.client.tools.Web;

public class EventService {

    private List<Event> events = new ArrayList<>();
    private boolean awaitingDeletion = false;
    private final EventsList eventsList;
    private final CalendarCustom calendarCustom;

    private final Observable eventUpdateManager = new EventObservable();
    private final Observer eventCreationObserver = new EventCreationObserver(this);
    private final Observer eventUpdateObserver = new EventUpdateObserver(this);
    private final Observer eventDeletionObserver = new EventDeletionObserver(this);

    public EventService(HomePage homePage) {
        eventsList = homePage.getEventsList();
        calendarCustom = homePage.getCalendarCustom();
        createSubscriptions();
    }

    private void createSubscriptions() {
        eventUpdateManager.subscribe("created", eventCreationObserver);
        eventUpdateManager.subscribe("updated", eventUpdateObserver);
        eventUpdateManager.subscribe("deleted", eventDeletionObserver);
    }

    public void updateEvents() {
        // repaint to reflect event data
        calendarCustom.updateCalendar(events);
        eventsList.updateEventsList(events);
    }

    private void onEventUpdated(Event event) {
        updateEvents();
        eventsList.onEventUpdated();
    }
    
    public boolean isUserAttendingEvent(Event event, User user) {
        for (User attendee : event.getAttendees()) {
            if (attendee.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    public void startFetchEvents() {
        new Thread(this::fetchEvents).start();
    }
    
    private void showErrorPopup(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void fetchEvents() {
        HttpResponse<String> response;
        try {
            response = Web.sendGetRequest("/private/event");
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
            return;
        }
        if (response.statusCode() != 200) {
            showErrorPopup(Web.getErrorMessage(response), "Failed to fetch events list");
            return;
        }
        events = Web.readResponseBody(response, new TypeReference<List<Event>>() {
        });
        updateEvents();
    }

    public void attemptRemoveEvent() {
        awaitingDeletion = true;
    }

    public Event getEvent(Long id) {
        for (Event event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    public boolean isAwaitingDeletion() {
        return awaitingDeletion;
    }

    public void addEvent(Event event) {
        events.add(event);
        onEventUpdated(event);
    }

    public void removeEvent(Long eventId) {
        events.removeIf(event -> event.getId().equals(eventId));
        updateEvents();
        awaitingDeletion = false;
    }

    public void updateEvent(Event event) {
        Long eventId = event.getId();
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            if (!e.getId().equals(eventId)) {
                continue;
            }
            events.set(i, event);
            onEventUpdated(event);
            return;
        }
        System.err.println("No event with id " + eventId);
    }
}
