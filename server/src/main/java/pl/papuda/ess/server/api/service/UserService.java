package pl.papuda.ess.server.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.model.error.UnauthorizedException;
import pl.papuda.ess.server.api.repo.UserRepository;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User ensureUserLoggedIn(Principal principal) throws UnauthorizedException {
        if (principal == null) {
            throw new UnauthorizedException("You are not logged in");
        }
        Optional<User> userData = userRepository.findByUsername(principal.getName());
        if (userData.isEmpty()) {
            throw new UnauthorizedException("Invalid logged in username");
        }
        return userData.get();
    }
}
