package pl.papuda.ess.server.api.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.RedirectView;

import lombok.RequiredArgsConstructor;
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

    @Value("classpath:/templates/verifyEmail.html")
    private Resource verifyEmailTemplate;

    @Autowired
    private EmailService emailService;

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

    private String readResource(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void sendChallengeEmail(String recipientAddress, String token) {
        String template = readResource(verifyEmailTemplate);
        String content = template.replace("{EMAIL}", recipientAddress).replace("{TOKEN}", token);
        emailService.sendEmail(recipientAddress, "Account Verification", content);
    }

    public RedirectView verifyEmail(String email, String token) {
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
}
