package pl.papuda.ess.client.services;

import pl.papuda.ess.client.tools.Web;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import pl.papuda.ess.client.model.body.StompResponse;
import uk.guzek.sac.AuthType;
import uk.guzek.sac.SubscriptionHandler;

public class StompClient extends uk.guzek.sac.StompClient {

    private boolean connected = false;
    private boolean closed = false;
    private boolean requestedClosure = false;

    
    public StompClient(URI serverUri, AuthType authType, String host) {
        super(serverUri, authType, host);
    }
    
    @Override
    public void onStompFrame(String frame, Map<String, String> headers, String body) {
        System.out.println("STOMP client received " + frame + " frame: " + (body.isBlank() ? "(no body)" : body));
    }

    @Override
    public void close() {
        requestedClosure = true;
        super.close();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Connection error", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void onClose(int code, String reason, boolean remotely) {
        if (requestedClosure && reason.isBlank()) {
            reason = "app-requested closure";
        }
        System.out.println("STOMP client closed " + (remotely ? "remotely" : "locally") + ": " + code + " | " + (reason.isBlank() ? "(unknown cause)" : reason));
        closed = true;
        connected = false;
        if (requestedClosure) {
            requestedClosure = false;
            return;
        }

        if (remotely) {
            showErrorDialog("Connection to the server lost. The connection will attempt to be re-established when performing an action.");
        } else {
            showErrorDialog("Could not connect to the server. Try again later.");
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("STOMP client error: " + ex);
    }

    @Override
    public int subscribe(String destination, SubscriptionHandler handler) {
        System.out.println("Subscribed to " + destination);
        return super.subscribe(destination, handler);
    }

    @Override
    public void sendText(String text, String destination) {
        super.sendText(text, destination);
        System.out.println("Sending '" + text + "' to " + destination);
    }

    @Override
    public void sendJson(Object object, String destination) throws JsonProcessingException {
        super.sendJson(object, destination);
        System.out.println("Sending JSON to " + destination);
    }

    @Override
    public void onConnected() {
        super.onConnected();
        connected = true;
        System.out.println("STOMP client connected!");
        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry<String, Consumer<Object>> entry : Web.getSubscriptions()) {
            String destination = entry.getKey();
            Consumer<Object> handler = entry.getValue();
            subscribe(destination, (Map<String, String> headers, String payload) -> {
//                System.out.println("Parsing body: " + payload);
                StompResponse response;
                try {
                    response = mapper.readValue(payload, new TypeReference<StompResponse>() {
                    });
                } catch (JsonProcessingException ex) {
                    System.err.println("Error handling message body: " + payload);
                    System.err.println(ex);
                    handler.accept(ex.getMessage());
                    return;
                }
                handler.accept(response.getBody());
            });
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean wasClosed() {
        return closed;
    }
}
