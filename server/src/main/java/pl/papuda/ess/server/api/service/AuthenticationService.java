package pl.papuda.ess.server.api.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.Role;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.AuthenticationRequest;
import pl.papuda.ess.server.api.model.body.AuthenticationResponse;
import pl.papuda.ess.server.api.model.body.RegistrationRequest;
import pl.papuda.ess.server.api.repo.UserRepository;

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
                .password(passwordEncoder.encode(request.getPassword())).role(Role.USER).emailVerified(false).build();
        userRepository.save(user);
        return getResponseFromUser(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        if (!user.isEmailVerified()) {
            throw new Error("'" + user.getEmail() + "' email address is not verified");
        }
        return getResponseFromUser(user);
    }

}
