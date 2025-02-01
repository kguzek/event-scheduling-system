package pl.papuda.ess.client.notificationStrategy;

import javax.swing.JOptionPane;
import pl.papuda.ess.client.interfaces.Strategy;

public class DialogNotificationStrategy implements Strategy {

    @Override
    public void sendNotification(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.DEFAULT_OPTION);
    }
}
