
package pl.papuda.ess.client.notificationStrategy;

import java.util.List;
import java.util.Arrays;
import pl.papuda.ess.client.interfaces.Strategy;


public class CombinedNotificationStrategy implements Strategy {

    private final List<Strategy> strategies;

    public CombinedNotificationStrategy(Strategy... strategies) {
        this.strategies = Arrays.asList(strategies);
    }

    @Override
    public void sendNotification(String title, String message) {
        for (Strategy strategy : strategies) {
            strategy.sendNotification(title, message);
        }
    }
}
