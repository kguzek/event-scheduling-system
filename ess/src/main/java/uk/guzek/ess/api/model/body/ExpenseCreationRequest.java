package uk.guzek.ess.api.model.body;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseCreationRequest {
  private String title;
  private int costCents;
  private Date datetime;
  private Long eventId;
}
