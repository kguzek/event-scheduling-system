package pl.papuda.ess.server.api.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class ErrorResponse {

    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> generate(String message) {
        return generate(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponse> generate(String message, HttpStatus code) {
        return new ResponseEntity<>(new ErrorResponse(message), code);
    }
}
