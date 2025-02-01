package pl.papuda.ess.server.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import pl.papuda.ess.server.common.RestResponse;
import pl.papuda.ess.server.api.model.User;
import pl.papuda.ess.server.api.repo.UserRepository;
import pl.papuda.ess.server.api.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    private String getDenialMessage(@NonNull HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        if (!authHeader.startsWith("Bearer ")) {
            return "Non-Bearer or otherwise malformed access token";
        }
        final String token = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            return e.getMessage();
        }
        if (username == null) {
            return "Malformed access token payload";
        }
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            return "Request is already authenticated";
        }
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return "Access token payload contains invalid user credentials";
        }
        User user;
        try {
            user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        } catch (Exception e) {
            return "User data not found in database";
        }
        if (!user.isEmailVerified()) {
            return "'" + user.getEmail() + "' email address is not verified";
        }
        if (!jwtService.isTokenValid(token, userDetails)) {
            return "Access token is invalid";
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username,
                null,
                userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authToken);
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String denialMessage = getDenialMessage(request);
        if (denialMessage == null) {
            filterChain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setHeader("WWW-Authenticate", "Bearer realm=/api/v1/auth/");
        // System.out.println("Denying request: " + denialMessage);
        response.getWriter().write(convertObjectToJson(new RestResponse(denialMessage)));
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return new ObjectMapper().writeValueAsString(object);
    }

}
