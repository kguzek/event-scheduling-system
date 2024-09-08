package uk.guzek.ess.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uk.guzek.ess.api.model.ErrorResponse;
import uk.guzek.ess.api.model.body.AuthenticationRequest;
import uk.guzek.ess.api.model.body.AuthenticationResponse;
import uk.guzek.ess.api.model.body.RegistrationRequest;
import uk.guzek.ess.api.repo.UserRepository;
import uk.guzek.ess.api.service.AuthenticationService;

import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[#?!@$ %^&*-]).{8,}$");
  private final AuthenticationService authenticationService;
  private final UserRepository userRepository;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
    if (!passwordPattern.matcher(request.getPassword()).matches()) {
      return ErrorResponse
          .generate("Password must be minimum 8 characters long, and contain a capital letter and a special character");
    }
    String username = request.getUsername();
    if (userRepository.findByUsername(username).isPresent()) {
      return ErrorResponse.generate(String.format("Username '%s' is taken", username));
    }
    return ResponseEntity.ok(authenticationService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }
}
