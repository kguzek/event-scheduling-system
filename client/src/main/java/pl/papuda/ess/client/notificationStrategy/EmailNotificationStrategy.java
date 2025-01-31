package pl.papuda.ess.client.notificationStrategy;

import java.io.IOException;
import javax.swing.JOptionPane;
import pl.papuda.ess.client.Web;
import pl.papuda.ess.client.interfaces.Strategy;
import pl.papuda.ess.client.model.Event;

public class EmailNotificationStrategy implements Strategy{

    @Override
    public void sendEventNotification(Event event) {
        String email = Web.user.getEmail();
        String startingIn = event.getStartTime();
        String text = String.format("Event %s is starting at %s!", event.getTitle(), startingIn);
        sendEmail(email, "Upcoming Event", text);
    }
    
    private void sendEmail(String email, String subject, String message) {
        String json = String.format("{\"recipient\":\"%s\",\"subject\":\"%s\",\"body\":\"%s\"}", email, subject, message);
        try {
            Web.sendPostRequest("/private/email/reminder", json);
        } catch (IOException | InterruptedException ex) {
            String errorMessage = String.format("Could not send email with subject '%s' to address <%s>.", subject, email);
            JOptionPane.showMessageDialog(null, errorMessage, "Could not send email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("Successfully sent email to " + email);
    }
}
