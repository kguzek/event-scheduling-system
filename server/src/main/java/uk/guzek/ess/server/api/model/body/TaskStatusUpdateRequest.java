package uk.guzek.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.guzek.ess.server.api.model.TaskStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusUpdateRequest {

    private TaskStatus status;
}
