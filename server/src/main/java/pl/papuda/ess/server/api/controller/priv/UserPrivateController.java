package pl.papuda.ess.server.api.controller.priv;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.body.UserPatchRequest;
import pl.papuda.ess.server.api.model.error.UnauthorizedException;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.UserService;
import pl.papuda.ess.server.common.RestResponse;

import java.security.Principal;
import java.util.Optional;

@RestController
@MessageMapping("/api/v1/private/user")
public class UserPrivateController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserPrivateController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody UserPatchRequest request, Principal principal) {
        User user;
        try {
            user = userService.ensureUserLoggedIn(principal);
        } catch (UnauthorizedException ex) {
            return ex.getResponse();
        }
        Optional<User> userData = userRepository.findById(user.getId());
        if (userData.isEmpty()) {
            return RestResponse.notFound("User");
        }
        User userObj = userData.get();
        userObj.setPreferredNotificationMethod(request.getNotificationMethod());
        return ResponseEntity.ok(userRepository.save(userObj));
    }
}
