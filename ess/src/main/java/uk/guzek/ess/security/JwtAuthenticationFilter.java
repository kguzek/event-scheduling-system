package uk.guzek.ess.security;

import java.io.IOException;

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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import uk.guzek.ess.api.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final UserDetailsService userDetailsService;

  private void authenticate(@NonNull HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer "))
      return;
    final String token = authHeader.substring(7);
    final String username = jwtService.extractUsername(token);
    if (username == null)
      return;
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext.getAuthentication() != null)
      return;
    UserDetails userDetails;
    try {
      userDetails = userDetailsService.loadUserByUsername(username);
    } catch (UsernameNotFoundException e) {
      return;
    }
    if (!jwtService.isTokenValid(token, userDetails))
      return;
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username,
        null,
        userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    securityContext.setAuthentication(authToken);
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    authenticate(request);
    filterChain.doFilter(request, response);
  }

}
