package pl.papuda.ess.server.api.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.papuda.ess.server.api.model.ErrorResponse;
import pl.papuda.ess.server.api.model.Role;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.repo.UserRepository;

@RestController
@RequestMapping("/api/v1/admin/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

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
            return ErrorResponse.generate("User not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userData.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userData = userRepo.findById(id);
        if (userData.isEmpty()) {
            return ErrorResponse.generate("User not found", HttpStatus.NOT_FOUND);
        }
        User userObj = userData.get();
        String email = user.getEmail();
        if (email == null) {
            return ErrorResponse.generate("User email not provided in request body");
        }
        Role role = user.getRole();
        if (role == null) {
            return ErrorResponse.generate("User role not provided in request body");
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
