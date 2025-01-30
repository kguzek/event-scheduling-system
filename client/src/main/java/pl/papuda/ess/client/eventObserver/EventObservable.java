package pl.papuda.ess.client.eventObserver;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import pl.papuda.ess.client.Web;
import pl.papuda.ess.client.interfaces.Observable;
import pl.papuda.ess.client.interfaces.Observer;

public class EventObservable implements Observable {

    public static Map<String, List<Observer>> observerMap = new HashMap<>();

    @Override
    public void subscribe(String endpoint, Observer listener) {
        List<Observer> observers = observerMap.get(endpoint);
        if (observers == null) {
            observerMap.put(endpoint, new ArrayList<>(Arrays.asList(listener)));
        } else {
            observers.add(listener);
        }
        String path = "/topic/events/" + endpoint;
        Web.subscribeStompResource(path, (Object body) -> notifyObservers(endpoint, body));
    }

    @Override
    public void unsubscribe(String endpoint, Observer listener) {
        List<Observer> observers = observerMap.get(endpoint);
        assert observers != null;
        observers.remove(listener);
    }

    @Override
    public void notifyObservers(String endpoint, Object messageBody) {
        List<Observer> observers = observerMap.get(endpoint);
        assert observers != null;
        for (Observer observer : observers) {
            observer.update(messageBody);
        }
    }
}
