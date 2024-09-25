package uk.guzek.ess.server.api.model.body;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.guzek.ess.server.api.model.TaskStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreationRequest {

    private String description;
    private Date deadline;
    private Long assigneeId;
    private Long eventId;
    private TaskStatus status;
}
