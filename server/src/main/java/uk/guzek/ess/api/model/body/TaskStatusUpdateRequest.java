package uk.guzek.ess.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.guzek.ess.api.model.TaskStatus;

@Data
@AllArgsConstructor
public class TaskStatusUpdateRequest {
  private TaskStatus status;
}