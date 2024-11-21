
package pl.papuda.ess.client.model.body;

import lombok.Data;

@Data
public class StompResponse {
    boolean ok;
    Object body;
}
