
package pl.papuda.ess.client.interfaces;

import pl.papuda.ess.client.model.Event;


public interface Strategy {
    public void sendEventNotification(Event event);
}
