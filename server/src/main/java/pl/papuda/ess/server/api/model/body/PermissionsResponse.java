package pl.papuda.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.papuda.ess.server.api.model.NotificationMethod;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionsResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private NotificationMethod preferredNotificationMethod;
}
