package uk.guzek.ess.server.api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.guzek.ess.server.api.model.Role;
import uk.guzek.ess.server.api.model.User;
import uk.guzek.ess.server.api.model.body.AuthenticationRequest;
import uk.guzek.ess.server.api.model.body.AuthenticationResponse;
import uk.guzek.ess.server.api.model.body.RegistrationRequest;
import uk.guzek.ess.server.api.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  private AuthenticationResponse getResponseFromUser(User user) {
    String token = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(token).userId(user.getId()).build();
  }

  public AuthenticationResponse register(RegistrationRequest request) {
    User user = User.builder().email(request.getEmail()).username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword())).role(Role.USER).build();
    userRepository.save(user);
    return getResponseFromUser(user);
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
    return getResponseFromUser(user);
  }

}
