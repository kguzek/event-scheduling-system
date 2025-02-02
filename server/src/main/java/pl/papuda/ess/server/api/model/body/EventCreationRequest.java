package pl.papuda.ess.server.api.model.body;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.papuda.ess.server.api.model.EventStatus;
import pl.papuda.ess.server.api.model.Frequency;
import pl.papuda.ess.server.api.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreationRequest {
    private String title;
    private String organiserName;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Location location;
    private Frequency frequency;
    private EventStatus status;
    private ZonedDateTime reminderTime;
    private String feedbackMessage;
    private Integer budgetCents;
}
