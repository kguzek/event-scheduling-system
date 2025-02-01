package pl.papuda.ess.client.notificationStrategy;

import javax.swing.JOptionPane;
import pl.papuda.ess.client.interfaces.Strategy;

public class PopupNotificationStrategy implements Strategy {

    @Override
    public void sendNotification(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
