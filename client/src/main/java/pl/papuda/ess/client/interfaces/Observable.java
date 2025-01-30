package pl.papuda.ess.client.interfaces;

public interface Observable {

    void subscribe(String endpoint, Observer observer);

    void unsubscribe(String endpoint, Observer observer);

    void notifyObservers(String endpoint, Object messageBody);
}
