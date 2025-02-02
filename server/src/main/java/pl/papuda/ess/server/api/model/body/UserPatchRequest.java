package pl.papuda.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.papuda.ess.server.api.model.NotificationMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchRequest {
    private NotificationMethod preferredNotificationMethod;
}
