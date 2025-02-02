package pl.papuda.ess.client.eventObserver;

import javax.swing.JOptionPane;
import pl.papuda.ess.client.interfaces.Observer;

public class EventNotificationObserver implements Observer {

    private void showInfoPopup(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void update(Object body) {
        showInfoPopup(body.toString(), "Event Starting");
    }

}
