package uk.guzek.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceCreationRequest {

    private Long eventId;
    private int goal;
    private int currentAmount;
    private String name;
    private String imageUrl;
}
