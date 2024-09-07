package uk.guzek.ess.api.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class ErrorResponse {
  private String message;

  public static ResponseEntity<ErrorResponse> generate(String message) {
    return generate(message, HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<ErrorResponse> generate(String message, HttpStatus code) {
    final ErrorResponse response = new ErrorResponse();
    response.setMessage(message);
    return new ResponseEntity<ErrorResponse>(response, code);
  }
}
