package pl.papuda.ess.server;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

public class EndpointChecker {
    private final DispatcherServlet servlet;

    public EndpointChecker(DispatcherServlet servlet) {
        this.servlet = servlet;
    }

    public boolean doesEndpointExist(HttpServletRequest request) {
        List<HandlerMapping> handlerMappings = servlet.getHandlerMappings();
        if (handlerMappings == null) {
            System.err.println("Servlet handler mappings are null");
            return false;
        }
        for (HandlerMapping handlerMapping : handlerMappings) {
            try {
                HandlerExecutionChain foundHandler = handlerMapping.getHandler(request);
                if (foundHandler != null) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
