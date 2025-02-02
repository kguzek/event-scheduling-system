package pl.papuda.ess.server.common.strategy;

import pl.papuda.ess.server.api.model.Event;

interface NotificationStrategy {
    void sendNotification(Event event, String message);
}
