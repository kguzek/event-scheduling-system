package pl.papuda.ess.server.api.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.papuda.ess.server.api.model.*;
import pl.papuda.ess.server.api.model.body.PermissionsResponse;
import pl.papuda.ess.server.api.model.error.UnauthorizedException;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.EmailService;
import pl.papuda.ess.server.api.service.UserService;
import pl.papuda.ess.server.common.ResponseUtilities;
import pl.papuda.ess.server.common.RestResponse;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/permissions")
public class PermissionsController {

    private final Map<Long, Long> userElevationRequestTimestamps = new HashMap<>();
    private static final int ELEVATION_REQUEST_COOLDOWN_MILLIS = 3600000; // 1 hour

    @Value("classpath:/templates/roleRequest.html")
    private Resource roleRequestTemplate;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;

    private boolean userRequestedElevationRecently(User user) {
        Long lastRequestTimestamp = userElevationRequestTimestamps.getOrDefault(user.getId(), 0L);
        Long currentTimestamp = System.currentTimeMillis();
        userElevationRequestTimestamps.putIfAbsent(user.getId(), currentTimestamp);
        return currentTimestamp - lastRequestTimestamp < ELEVATION_REQUEST_COOLDOWN_MILLIS;
    }

    @GetMapping
    public ResponseEntity<?> getUserPermissions(Principal principal) {
        User user;
        try {
            user = userService.ensureUserLoggedIn(principal);
        } catch (UnauthorizedException e) {
            return e.getResponse();
        }
        PermissionsResponse response = PermissionsResponse.builder().id(user.getId()).email(user.getEmail())
                .username(user.getUsername()).role(user.getRole().name()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/roles/staff")
    public ResponseEntity<RestResponse> requestStaffRole(Principal principal) {
        User user;
        try {
            user = userService.ensureUserLoggedIn(principal);
        } catch (UnauthorizedException e) {
            return e.getResponse();
        }
        if (userRequestedElevationRecently(user)) {
            return RestResponse.generate("You have already requested a staff role recently", HttpStatus.TOO_MANY_REQUESTS);
        }

        String subject = String.format("Staff Privilege Request for %s", user.getUsername());
        String template = ResponseUtilities.readResource(roleRequestTemplate);
        String content = template.replace("{REQUESTING_USERNAME}", user.getUsername());

        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);
        int sentEmails = 0;
        int numAdminUsers = adminUsers.size();
        for (User adminUser : adminUsers) {
            boolean success = emailService.sendEmail(adminUser.getEmail(), subject, content);
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
