package uk.guzek.ess.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uk.guzek.ess.api.model.AuthenticationRequest;
import uk.guzek.ess.api.model.AuthenticationResponse;
import uk.guzek.ess.api.model.ErrorResponse;
import uk.guzek.ess.api.model.RegistrationRequest;
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
      return ErrorResponse.generate("Password doesn't match regex");
    }
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      return ErrorResponse.generate("Username is taken");
    }
    return ResponseEntity.ok(authenticationService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }
}
