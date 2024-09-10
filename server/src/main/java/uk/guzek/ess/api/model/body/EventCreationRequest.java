package uk.guzek.ess.api.model.body;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.guzek.ess.api.model.EventStatus;
import uk.guzek.ess.api.model.Frequency;
import uk.guzek.ess.api.model.Location;

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
