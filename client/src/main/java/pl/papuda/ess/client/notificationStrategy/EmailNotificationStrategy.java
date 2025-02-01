package pl.papuda.ess.client.notificationStrategy;

import java.io.IOException;
import javax.swing.JOptionPane;
import pl.papuda.ess.client.tools.Web;
import pl.papuda.ess.client.interfaces.Strategy;

public class EmailNotificationStrategy implements Strategy{

    @Override
    public void sendNotification(String title, String message) {
        String email = Web.user.getEmail();
        sendEmail(email, title, message);
    }
    
    private void sendEmail(String email, String subject, String message) {
        String json = String.format("{\"recipient\":\"%s\",\"subject\":\"%s\",\"body\":\"%s\"}", email, subject, message);
        try {
            Web.sendPostRequest("/private/event/reminder/email", json);
        } catch (IOException | InterruptedException ex) {
            String errorMessage = String.format("Could not send email with subject '%s' to address <%s>.", subject, email);
            JOptionPane.showMessageDialog(null, errorMessage, "Could not send email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("Successfully sent email to " + email);
    }
}
