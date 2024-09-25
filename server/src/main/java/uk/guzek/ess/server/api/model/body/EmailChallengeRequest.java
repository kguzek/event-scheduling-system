package uk.guzek.ess.server.api.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailChallengeRequest {

    private String email;
    private String token;
}
