package pl.papuda.ess.server.common.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.papuda.ess.server.api.model.Event;
import pl.papuda.ess.server.api.model.body.websocket.StompResponse;

@Service
@RequiredArgsConstructor
public class PopupNotificationStrategy implements NotificationStrategy {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Event event, String message) {
        String path = String.format("/topic/events/%d/reminder", event.getId());
        System.out.println("Broadcasting notification to " + path);
        messagingTemplate.convertAndSend(path, new StompResponse<>(true, message));
    }
}
