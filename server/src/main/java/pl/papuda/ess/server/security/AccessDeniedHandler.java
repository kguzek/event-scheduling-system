package pl.papuda.ess.server.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.papuda.ess.server.common.ResponseUtilities;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String data = ResponseUtilities.json("message", "You are not authorised to access this resource", "error", accessDeniedException.getMessage());
        ResponseUtilities.send(response, data);
    }
}