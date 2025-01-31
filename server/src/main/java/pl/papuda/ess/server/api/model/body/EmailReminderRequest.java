package pl.papuda.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailReminderRequest {

    private String recipient;
    private String subject;
    private String body;
}
