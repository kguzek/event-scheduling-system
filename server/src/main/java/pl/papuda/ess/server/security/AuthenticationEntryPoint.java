package pl.papuda.ess.server.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;
import pl.papuda.ess.server.EndpointChecker;
import pl.papuda.ess.server.common.ResponseUtilities;

import java.io.IOException;

@Component
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    private final EndpointChecker endpointChecker;

    public AuthenticationEntryPoint(DispatcherServlet servlet) {
        this.endpointChecker = new EndpointChecker(servlet);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        String data;
        if (!endpointChecker.doesEndpointExist(request)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            data = ResponseUtilities.json("message", "The requested resource could not be located");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            data = ResponseUtilities.json("message", "This resource is only accessible to authenticated requests", "error", authException.getMessage());
        }
        ResponseUtilities.send(response, data);
    }
}
