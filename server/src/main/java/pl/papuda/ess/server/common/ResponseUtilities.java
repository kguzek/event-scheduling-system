package pl.papuda.ess.server.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtilities {

    public static String json(String... values) {

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("There must be an even number of arguments passed to json(String... values)");
        }

        // Create a Map from the key-value pairs
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException ex) {
            System.err.println("JSON processing error: " + ex);
            return "{}";  // Return empty JSON in case of error
        }
    }

    public static void send(HttpServletResponse response, String data) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(data);
        writer.flush();
    }
}
