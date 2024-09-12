package uk.guzek.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.guzek.ess.server.api.model.TaskStatus;

@Data
@AllArgsConstructor
public class TaskStatusUpdateRequest {
  private TaskStatus status;
}