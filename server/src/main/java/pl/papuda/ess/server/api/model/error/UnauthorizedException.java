package pl.papuda.ess.server.api.model.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.papuda.ess.server.common.RestResponse;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public ResponseEntity<RestResponse> getResponse() {
        return RestResponse.generate(getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
