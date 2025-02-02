
package pl.papuda.ess.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PartialEvent {
    private String title;
    private String organiserName;
    private String startTime;
    private String endTime;
    private Location location;
    private String frequency;
    private String status;
    private String reminderTime;
    private String feedbackMessage;
    private Integer budgetCents;
}
