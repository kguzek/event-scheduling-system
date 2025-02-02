package pl.papuda.ess.server.tasks;

import pl.papuda.ess.server.api.model.User;

interface NotificationStrategy {
    void sendNotification(User user, String title, String message);
}
