package pl.papuda.ess.server.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.papuda.ess.server.config.EndpointChecker;
import pl.papuda.ess.server.common.ResponseUtilities;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    private final EndpointChecker endpointChecker;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException/*, ServletException*/ {
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
