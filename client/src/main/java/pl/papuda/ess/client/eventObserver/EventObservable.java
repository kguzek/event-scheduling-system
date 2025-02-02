package pl.papuda.ess.client.eventObserver;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import javax.swing.JOptionPane;
import pl.papuda.ess.client.tools.Web;
import pl.papuda.ess.client.interfaces.Observable;
import pl.papuda.ess.client.interfaces.Observer;

public class EventObservable implements Observable {

    private static Map<String, List<Observer>> observerMap = new HashMap<>();

    private void showErrorMessage(String errorMessage) {
        System.err.println("Observer error message: " + errorMessage);
        // eventsList1.setErrorText(errorMessage);
        JOptionPane.showMessageDialog(null, errorMessage, "Problem performing action", JOptionPane.ERROR_MESSAGE);
    }

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
        if (observers.isEmpty()) {
            Web.unsubscribeStompResource("/topic/events/" + endpoint);
        }
    }

    @Override
    public void notifyObservers(String endpoint, Object messageBody) {
        List<Observer> observers = observerMap.get(endpoint);
        assert observers != null;
        for (Observer observer : observers) {
            if (!endpoint.endsWith("/reminder") && messageBody instanceof String errorMessage) {
                showErrorMessage(errorMessage);
                continue;
            }
            observer.update(messageBody);
        }
    }
}
