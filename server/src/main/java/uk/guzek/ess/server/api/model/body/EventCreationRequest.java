package uk.guzek.ess.server.api.model.body;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.guzek.ess.server.api.model.EventStatus;
import uk.guzek.ess.server.api.model.Frequency;
import uk.guzek.ess.server.api.model.Location;

@Data
@AllArgsConstructor
public class EventCreationRequest {
  private String title;
  private String organiserName;
  private Date startTime;
  private Date endTime;
  private Location location;
  private Frequency frequency;
  private EventStatus status;
}
