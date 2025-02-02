package pl.papuda.ess.server.api.controller.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.papuda.ess.server.api.service.EmailService;
import pl.papuda.ess.server.common.ResponseUtilities;
import pl.papuda.ess.server.common.RestResponse;
import pl.papuda.ess.server.api.model.Role;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.repo.UserRepository;

@RestController
@RequestMapping("/api/v1/admin/user")
public class UserAdminController {

    private final UserRepository userRepo;
    private final EmailService emailService;

    @Value("classpath:/templates/userPromoted.html")
    private Resource userPromotedTemplate;

    public UserAdminController(UserRepository userRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepo.findAll().forEach(user -> {
            ResponseEntity.ok(user);
            users.add(user);
        });
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> userData = userRepo.findById(id);
        if (userData.isEmpty()) {
            return RestResponse.notFound("User");
        }
        return ResponseEntity.ok(userData.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user, Principal principal) {
        Optional<User> userData = userRepo.findById(id);
        if (userData.isEmpty()) {
            return RestResponse.notFound("User");
        }
        User userObj = userData.get();
        String email = user.getEmail();
        if (email == null) {
            return RestResponse.badRequest("User email not provided in request body");
        }
        Role role = user.getRole();
        if (role == null) {
            return RestResponse.badRequest("User role not provided in request body");
        }
        if (userObj.getRole().equals(Role.USER) && role.equals(Role.STAFF)) {
            String template = ResponseUtilities.readResource(userPromotedTemplate);
            String content = template.replace("{GRANTING_USERNAME}", principal.getName());
            emailService.sendEmail(userObj.getEmail(), "New Privileges Granted", content);
        }
        userObj.setEmail(email);
        userObj.setRole(role);
        return ResponseEntity.ok(userRepo.save(userObj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
