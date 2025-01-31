package pl.papuda.ess.client.notificationStrategy;

import javax.swing.JOptionPane;
import pl.papuda.ess.client.interfaces.Strategy;
import pl.papuda.ess.client.model.Event;

public class DialogNotificationStrategy implements Strategy {

    @Override
    public void sendEventNotification(Event event) {
        String startingIn = event.getStartTime();
        String text = String.format("Event %s is starting at %s!", event.getTitle(), startingIn);
        JOptionPane.showMessageDialog(null, text, "Event starting!", JOptionPane.DEFAULT_OPTION);
    }
}
