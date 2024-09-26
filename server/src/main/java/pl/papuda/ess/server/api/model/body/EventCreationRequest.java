package pl.papuda.ess.server.api.model.body;

import java.util.Date;

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
    private Date startTime;
    private Date endTime;
    private Location location;
    private Frequency frequency;
    private EventStatus status;
}
