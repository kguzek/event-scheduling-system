package pl.papuda.ess.server.api.model.body;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCreationRequest {

    private String title;
    private int costCents;
    private Date datetime;
    private Long eventId;
}
