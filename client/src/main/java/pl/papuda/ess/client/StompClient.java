package pl.papuda.ess.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URI;
import java.util.Map;
import uk.guzek.sac.SubscriptionHandler;

public class StompClient extends uk.guzek.sac.StompClient {
    
    private boolean connected = false;
    private boolean disconnected = false;

    public StompClient(URI serverUri, Map<String, String> headers, String host) {
        super(serverUri, headers, host);
    }
    
    @Override
    public void onStompFrame(String frame, Map<String, String> headers, String body) {
        System.out.println("STOMP client received " + frame + " frame: " + body);
    }

    @Override
    public void onClose(int code, String reason, boolean remotely) {
        System.out.println("STOMP client closed: " + code + " " + reason);
        disconnected = true;
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("STOMP client error: " + ex);
    }
    
    @Override
    public int subscribe(String destination,  SubscriptionHandler handler) {
        System.out.println("subscribed to " + destination);
        return super.subscribe(destination, handler);
    }
    
    @Override
    public void sendText(String text, String destination) {
        super.sendText(text, destination);
        System.out.println("sending '" + text + "' to " + destination);
    }
    
    @Override
    public void sendJson(Object object, String destination) throws JsonProcessingException {
        super.sendJson(object, destination);
        System.out.println("sending JSON to " + destination);
    }
    
    @Override
    public void onConnected() {
        super.onConnected();
        connected = true;
        System.out.println("STOMP client connected!");
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public boolean isDisconnected() {
        return disconnected;
    }
}
