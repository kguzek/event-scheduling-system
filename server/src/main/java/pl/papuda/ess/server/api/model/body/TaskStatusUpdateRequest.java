package pl.papuda.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.papuda.ess.server.api.model.TaskStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusUpdateRequest {

    private TaskStatus status;
}
