package pl.papuda.ess.server.api.controller;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.AuthenticationRequest;
import pl.papuda.ess.server.api.model.body.AuthenticationResponse;
import pl.papuda.ess.server.api.model.body.EmailChallengeRequest;
import pl.papuda.ess.server.api.model.body.RegistrationRequest;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.AuthenticationService;
import pl.papuda.ess.server.api.service.JwtService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[#?!@$ %^&*-]).{8,}$");
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        if (!passwordPattern.matcher(request.getPassword()).matches()) {
            return ErrorResponse
                    .generate(
                            "Password must be minimum 8 characters long, and contain a capital letter and a special character");
        }
        String username = request.getUsername();
        if (username == null || username.length() == 0) {
            return ErrorResponse.generate("Username cannot be empty");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return ErrorResponse.generate(String.format("Username '%s' is taken", username));
        }
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (Error ex) {
            return ErrorResponse.generate(ex.getMessage());
        }
    }

    @PostMapping("/email/challenge")
    public ResponseEntity<?> createEmailChallenge(@RequestBody EmailChallengeRequest request) {
        String email = request.getEmail();
        String token = jwtService.generateEmailToken(email);
        // TODO: send email with token
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<?> verifyEmail(@RequestBody EmailChallengeRequest request) {
        String token = request.getToken();
        String email = request.getEmail();
        if (!jwtService.isEmailTokenValid(token, email)) {
            return ErrorResponse.generate("Invalid email token");
        }
        return ResponseEntity.ok("Verified");
    }

    @GetMapping("/email/poll")
    public ResponseEntity<?> checkEmailVerified(@RequestParam String email) {
        Optional<User> userData = userRepository.findByEmail(email);
        if (userData.isEmpty()) {
            return ErrorResponse.generate("Invalid email");
        }
        User user = userData.get();
        if (user.isEmailVerified()) {
            return ResponseEntity.ok().build();
        }
        return ErrorResponse.generate("Email not verified");
    }
}
