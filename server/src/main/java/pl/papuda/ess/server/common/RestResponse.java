package pl.papuda.ess.server.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record RestResponse(String message) {

    public static ResponseEntity<RestResponse> ok(String message) {
        return generate(message, HttpStatus.OK);
    }

    public static ResponseEntity<RestResponse> badRequest(String message) {
        return generate(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<RestResponse> notFound(String resourceName) {
        return generate(resourceName + " not found", HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<RestResponse> forbidden(String message) {
        return generate(message, HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<RestResponse> generate(String message, HttpStatus code) {
        return new ResponseEntity<>(new RestResponse(message), code);
    }
}
