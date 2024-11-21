package pl.papuda.ess.server.api.model.body.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StompResponse<T> {
    boolean ok;
    T body;
}
