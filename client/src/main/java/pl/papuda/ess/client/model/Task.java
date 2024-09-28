package pl.papuda.ess.client.model;

import lombok.Data;

@Data
public class Task {
    private Long id;
    private String description;
    private User assignee;
    private String deadline;
    private String status;
    private String additionalInformation;
}
