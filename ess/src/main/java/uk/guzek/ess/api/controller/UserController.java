package uk.guzek.ess.api.controller;

import uk.guzek.ess.api.model.ErrorResponse;
import uk.guzek.ess.api.model.User;
import uk.guzek.ess.api.repo.UserRepository;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  @Autowired
  private UserRepository userRepo;

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    // List<User> users = new ArrayList<>();
    // userRepo.findAll().forEach(users::add);
    // return ResponseEntity.ok(users);
    return ResponseEntity.ok(userRepo.findAll());
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
    userObj.setEmail(user.getEmail());
    userObj = userRepo.save(userObj);
    return ResponseEntity.ok(userObj);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userRepo.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
