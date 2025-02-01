package pl.papuda.ess.server.api.controller.priv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.papuda.ess.server.api.model.*;
import pl.papuda.ess.server.api.model.body.EmailReminderRequest;
import pl.papuda.ess.server.api.model.body.PermissionsResponse;
import pl.papuda.ess.server.api.model.error.UnauthorizedException;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.EmailService;
import pl.papuda.ess.server.common.RestResponse;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/private")
public class PermissionsController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    private final Map<Long, Long> userElevationRequestTimestamps = new HashMap<>();

    private static final int ELEVATION_REQUEST_COOLDOWN_MILLIS = 3600000; // 1 hour

    private User ensureUserLoggedIn(Principal principal) throws UnauthorizedException {
        if (principal == null) {
            throw new UnauthorizedException("You are not logged in");
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            throw new UnauthorizedException("Invalid logged in username");
        }
        return userData.get();
    }

    private boolean userRequestedElevationRecently(User user) {
        Long lastRequestTimestamp = userElevationRequestTimestamps.get(user.getId());
        if (lastRequestTimestamp == null) return false;
        Long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastRequestTimestamp < ELEVATION_REQUEST_COOLDOWN_MILLIS) {
            return true;
        }
        userElevationRequestTimestamps.put(user.getId(), currentTimestamp);
        return false;
    }

    private ResponseEntity<RestResponse> getEmailResultResponse(boolean success) {
        return success
                ? RestResponse.ok("Email message sent successfully")
                : RestResponse.generate("An unknown error occurred while sending the email message", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping("/email/reminder")
    public ResponseEntity<RestResponse> sendReminderEmail(@RequestBody EmailReminderRequest request) {
        boolean success = emailService.sendEmail(request.getRecipient(), request.getSubject(), request.getBody());
        return getEmailResultResponse(success);
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions(Principal principal) {
        User user;
        try {
            user = ensureUserLoggedIn(principal);
        } catch (UnauthorizedException e) {
            return e.getResponse();
        }
        PermissionsResponse response = PermissionsResponse.builder().id(user.getId()).email(user.getEmail())
                .username(user.getUsername()).role(user.getRole().name()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/permissions/roles/staff")
    public ResponseEntity<RestResponse> requestStaffRole(Principal principal) {
        User user;
        try {
            user = ensureUserLoggedIn(principal);
        } catch (UnauthorizedException e) {
            return e.getResponse();
        }
        if (userRequestedElevationRecently(user)) {
            return RestResponse.generate("You have already requested a staff role recently", HttpStatus.TOO_MANY_REQUESTS);
        }

        String subject = String.format("Staff Privilege Request for %s", user.getUsername());
        String message = String.format("User %s is requesting to be granted the staff role. ", user.getUsername());

        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);
        int sentEmails = 0;
        int numAdminUsers = adminUsers.size();
        for (User adminUser : adminUsers) {
            boolean success = emailService.sendEmail(adminUser.getEmail(), subject, message);
            if (success) {
                sentEmails++;
            }
        }
        if (sentEmails == numAdminUsers) {
            return RestResponse.ok(String.format("Sent request emails to all %d admin users", numAdminUsers));
        }
        String errorMessage = String.format("Sent request emails to %d of %d admin users", sentEmails, numAdminUsers);
        return RestResponse.generate(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
