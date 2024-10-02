package pl.papuda.ess.server.api.controller;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.AuthenticationRequest;
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
        String email = request.getEmail();
        if (email == null || email.length() == 0) {
            return ErrorResponse.generate("Email cannot be empty");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ErrorResponse.generate(String.format("An account for '%s' already exists.", email));
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
        authenticationService.sendChallengeEmail(email, token);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/email/verify/{email}/{token}")
    public RedirectView verifyEmail(@PathVariable String email, @PathVariable String token) {
        RedirectView redirectView = new RedirectView("/invalidEmailToken.html");
        System.out.println("REQUEST for verification " + email);
        try {
            if (!jwtService.isEmailTokenValid(token, email)) {
                return redirectView;
            }
        } catch (Exception ex) {
            return redirectView;
        }
        Optional<User> userData = userRepository.findByEmail(email);
        if (userData.isEmpty()) {
            return redirectView;
        }
        User user = userData.get();
        if (user.isEmailVerified()) {
            return redirectView;
        }
        user.setEmailVerified(true);
        System.out.println("UPDATING USER EMAIL VERIFIED STATE TO TRUE");
        userRepository.save(user);
        System.out.println("SAVED NEW USER PROPERTIES");
        redirectView.setUrl("/verificationSuccessful.html");
        return redirectView;
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
