package pl.papuda.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceCreationRequest {

    private Long eventId;
    private int goal;
    private int currentAmount;
    private String name;
    private String imageUrl;
}
