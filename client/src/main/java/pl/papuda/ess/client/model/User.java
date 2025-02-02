
package pl.papuda.ess.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String preferredNotificationMethod;
}
